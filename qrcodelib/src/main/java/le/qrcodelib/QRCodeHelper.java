
package le.qrcodelib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.InputStream;
import java.util.Hashtable;

public class QRCodeHelper {
    private static final String TAG = "QRCodeHelper";

    private static final int DEF_CONTENT_COLOR = 0xff000000;
    private static final int DEF_BACKGROUND_COLOR = 0xffffffff;

    private int mContentColor = DEF_CONTENT_COLOR;
    private int mBackgroundColor = DEF_BACKGROUND_COLOR;

    public QRCodeHelper() {
    }

    /**
     * set QRCode Color
     *
     * @param contentColor
     */
    public void setContentColor(int contentColor) {
        mContentColor = contentColor;
    }

    /**
     * set Background Color
     *
     * @param bgColor
     */
    public void setBackGroundColor(int bgColor) {
        mBackgroundColor = bgColor;
    }

    /**
     * create QRcode image,default: width=300 height=300 margin=0
     *
     * @param url
     * @return
     * @throws Exception
     */
    public Bitmap encodeQRCode(String url) throws Exception {
        return encodeQRCode(url, 300, 300, 0);
    }

    /**
     * create QRcode image,default: margin=0
     *
     * @param url
     * @param length
     * @return
     * @throws Exception
     */
    public Bitmap encodeQRCode(String url, int length) throws Exception {
        return encodeQRCode(url, length, length, 0);
    }

    /**
     * create QRcode image
     *
     * @param url
     * @param length :width==height
     * @return
     */
    public Bitmap encodeQRCode(String url, int length, int margin) throws Exception {
        return encodeQRCode(url, length, length, margin);
    }

    /**
     * create QRcode image with logo
     *
     * @param url
     * @param width
     * @param height
     * @param margin
     * @param size
     * @param logoBmp
     * @return
     * @throws Exception
     */
    public Bitmap encodeQRCodeWithLogo(String url, int width, int height, int margin, int size, Bitmap logoBmp) throws Exception {
        Bitmap bitmap = encodeQRCode(url, width, height, margin);
        bitmap = addLogo(bitmap, logoBmp, size);
        return bitmap;
    }

    /**
     * create QRCode image
     *
     * @param url
     * @param width
     * @param height
     * @param margin
     * @return
     * @throws Exception
     */
    public Bitmap encodeQRCode(String url, int width, int height, int margin) throws Exception {
        Bitmap bitmap = null;
        if (url == null || "".equals(url) || url.length() < 1) {
            throw new Exception("The params(String type) can not is null.");
        }
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        if (margin >= 0) {
            hints.put(EncodeHintType.MARGIN, margin);
        }
        BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (bitMatrix.get(x, y)) {
                    pixels[y * width + x] = mContentColor;
                } else {
                    pixels[y * width + x] = mBackgroundColor;
                }
            }
        }
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * decode QRcode according to the file
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    public String decodeQRCode(String filePath) throws Exception {
        if (TextUtils.isEmpty(filePath) || !new File(filePath).exists()) {
            throw new IllegalArgumentException("The file does not exist, please check it.");
        }
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        if (bitmap == null) {
            throw new Exception("The file can not be converted to a bitmap type, please check it.");
        }
        return decodeBitmap(bitmap);
    }

    /**
     * decode QRcode according to the inputStream
     *
     * @param inputStream
     * @return
     * @throws Exception
     */
    public String decodeQRCode(InputStream inputStream) throws Exception {
        if (inputStream == null) {
            throw new IllegalArgumentException("The inputStream is null.");
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        if (bitmap == null) {
            throw new Exception("The inputStream can not be converted to a bitmap type, please check it.");
        }
        return decodeBitmap(bitmap);
    }

    /**
     * decode QRcode according to the bitmap
     *
     * @param bitmap
     * @return
     */
    public String decodeQRCode(Bitmap bitmap) throws Exception {
        if (bitmap == null) {
            throw new IllegalArgumentException("The params(Bitmap type) can not is null.");
        }
        return decodeBitmap(bitmap);
    }

    /**
     * decode bitmap
     *
     * @param bitmap
     * @return String
     */
    private String decodeBitmap(Bitmap bitmap) throws NotFoundException, ChecksumException, FormatException {
        Log.d(TAG, "decodeBitmap: ");
        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        Reader reader = new MultiFormatReader();
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
        Result result = reader.decode(binaryBitmap, hints);
        return result == null ? null : result.getText();
    }

    /**
     * QR code with logo
     *
     * @param src
     * @param logo
     * @param size
     * @return
     */
    private Bitmap addLogo(Bitmap src, Bitmap logo, int size) {
        if (src == null) {
            return null;
        }
        if (logo == null) {
            return src;
        }

        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }
        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }
        //logo大小为二维码整体大小的1/size
        float scaleFactor = srcWidth * 1.0f / size / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }
}
