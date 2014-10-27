package com.jaks.alvarp.drawthing.fileWorkarounds;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alvaro on 10/27/14.
 */

public final class ImageWorkaround {
    private static final String CONTENT_AUTHORITY_SLASH = "content://" + "media" + "/";

    public interface ImageColumns extends MediaStore.MediaColumns {
        /**
         * The description of the image
         * <P>Type: TEXT</P>
         */
        public static final String DESCRIPTION = "description";

        /**
         * The picasa id of the image
         * <P>Type: TEXT</P>
         */
        public static final String PICASA_ID = "picasa_id";

        /**
         * Whether the video should be published as public or private
         * <P>Type: INTEGER</P>
         */
        public static final String IS_PRIVATE = "isprivate";

        /**
         * The latitude where the image was captured.
         * <P>Type: DOUBLE</P>
         */
        public static final String LATITUDE = "latitude";

        /**
         * The longitude where the image was captured.
         * <P>Type: DOUBLE</P>
         */
        public static final String LONGITUDE = "longitude";

        /**
         * The date & time that the image was taken in units
         * of milliseconds since jan 1, 1970.
         * <P>Type: INTEGER</P>
         */
        public static final String DATE_TAKEN = "datetaken";

        /**
         * The orientation for the image expressed as degrees.
         * Only degrees 0, 90, 180, 270 will work.
         * <P>Type: INTEGER</P>
         */
        public static final String ORIENTATION = "orientation";

        /**
         * The mini thumb id.
         * <P>Type: INTEGER</P>
         */
        public static final String MINI_THUMB_MAGIC = "mini_thumb_magic";

        /**
         * The bucket id of the image. This is a read-only property that
         * is automatically computed from the DATA column.
         * <P>Type: TEXT</P>
         */
        public static final String BUCKET_ID = "bucket_id";

        /**
         * The bucket display name of the image. This is a read-only property that
         * is automatically computed from the DATA column.
         * <P>Type: TEXT</P>
         */
        public static final String BUCKET_DISPLAY_NAME = "bucket_display_name";
    }

    public static final class Media implements ImageColumns {
        public static final Cursor query(ContentResolver cr, Uri uri, String[] projection) {
            return cr.query(uri, projection, null, null, DEFAULT_SORT_ORDER);
        }

        public static final Cursor query(ContentResolver cr, Uri uri, String[] projection,
                                         String where, String orderBy) {
            return cr.query(uri, projection, where,
                    null, orderBy == null ? DEFAULT_SORT_ORDER : orderBy);
        }

        public static final Cursor query(ContentResolver cr, Uri uri, String[] projection,
                                         String selection, String[] selectionArgs, String orderBy) {
            return cr.query(uri, projection, selection,
                    selectionArgs, orderBy == null ? DEFAULT_SORT_ORDER : orderBy);
        }

        /**
         * Retrieves an image for the given url as a {@link android.graphics.Bitmap}.
         *
         * @param cr  The content resolver to use
         * @param url The url of the image
         * @throws java.io.FileNotFoundException
         * @throws java.io.IOException
         */
        public static final Bitmap getBitmap(ContentResolver cr, Uri url)
                throws FileNotFoundException, IOException {
            InputStream input = cr.openInputStream(url);
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            input.close();
            return bitmap;
        }

        /**
         * Insert an image and create a thumbnail for it.
         *
         * @param cr          The content resolver to use
         * @param imagePath   The path to the image to insert
         * @param name        The name of the image
         * @param description The description of the image
         * @return The URL to the newly created image
         * @throws FileNotFoundException
         */
        public static final String addImage(ContentResolver cr, String imagePath,
                                               String name, String description) throws FileNotFoundException {
            // Check if file exists with a FileInputStream
            FileInputStream stream = new FileInputStream(imagePath);
            try {
                Bitmap bm = BitmapFactory.decodeFile(imagePath);
                String ret = addImage(cr, bm, name, description);
                bm.recycle();
                return ret;
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }

        private static final Bitmap StoreThumbnail(
                ContentResolver cr,
                Bitmap source,
                long id,
                float width, float height,
                int kind) {
            // create the matrix to scale it
            Matrix matrix = new Matrix();

            float scaleX = width / source.getWidth();
            float scaleY = height / source.getHeight();

            matrix.setScale(scaleX, scaleY);

            Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                    source.getWidth(),
                    source.getHeight(), matrix,
                    true);

            ContentValues values = new ContentValues(4);
            values.put(MediaStore.Images.Thumbnails.KIND, kind);
            values.put(MediaStore.Images.Thumbnails.IMAGE_ID, (int) id);
            values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.getHeight());
            values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.getWidth());

            Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

            try {
                OutputStream thumbOut = cr.openOutputStream(url);

                thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
                thumbOut.close();
                return thumb;
            } catch (FileNotFoundException ex) {
                return null;
            } catch (IOException ex) {
                return null;
            }
        }

        /**
         * Insert an image and create a thumbnail for it.
         *
         * @param cr          The content resolver to use
         * @param source      The stream to use for the image
         * @param title       The name of the image
         * @param description The description of the image
         * @return The URL to the newly created image, or <code>null</code> if the image failed to be stored
         * for any reason.
         */
        public static final String addImage(ContentResolver cr, Bitmap source,
                                               String title, String description) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, title);
            values.put(MediaStore.Images.Media.DESCRIPTION, description);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

            Uri url = null;
            String stringUrl = null;    /* value to be returned */

            try {
                url = cr.insert(EXTERNAL_CONTENT_URI, values);

                if (source != null) {
                    OutputStream imageOut = cr.openOutputStream(url);
                    try {
                        source.compress(Bitmap.CompressFormat.PNG, 100, imageOut);
                    } finally {
                        imageOut.close();
                    }

                    long id = ContentUris.parseId(url);
                    // Wait until MINI_KIND thumbnail is generated.
                    Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id,
                            MediaStore.Images.Thumbnails.MINI_KIND, null);
                    // This is for backward compatibility.
                    Bitmap microThumb = StoreThumbnail(cr, miniThumb, id, 50F, 50F,
                            MediaStore.Images.Thumbnails.MICRO_KIND);
                } else {
                    cr.delete(url, null, null);
                    url = null;
                }
            } catch (Exception e) {
                if (url != null) {
                    cr.delete(url, null, null);
                    url = null;
                }
            }

            if (url != null) {
                stringUrl = url.toString();
            }

            return stringUrl;
        }

        /**
         * Get the content:// style URI for the image media table on the
         * given volume.
         *
         * @param volumeName the name of the volume to get the URI for
         * @return the URI to the image media table on the given volume
         */
        public static Uri getContentUri(String volumeName) {
            return Uri.parse(CONTENT_AUTHORITY_SLASH + volumeName +
                    "/images/media");
        }

        /**
         * The content:// style URI for the internal storage.
         */
        public static final Uri INTERNAL_CONTENT_URI =
                getContentUri("internal");

        /**
         * The content:// style URI for the "primary" external storage
         * volume.
         */
        public static final Uri EXTERNAL_CONTENT_URI =
                getContentUri("external");

        /**
         * The MIME type of of this directory of
         * images.  Note that each entry in this directory will have a standard
         * image MIME type as appropriate -- for example, image/jpeg.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/image";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = ImageColumns.BUCKET_DISPLAY_NAME;
    }
}
