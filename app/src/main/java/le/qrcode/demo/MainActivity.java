
package le.qrcode.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import le.qrcodelib.QRCodeHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private QRCodeHelper mQrCodeHelper;

    private TextView mEncodeTvLogo;
    private Button mEncodeBtnLogo;
    private ImageView mEncodeIvLogo;

    private TextView mEncodeTextView;
    private Button mEncodeButton;
    private ImageView mEncodeImageView;

    private ImageView mDecodeImageView;
    private Button mDecodeButton;
    private TextView mDecodeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQrCodeHelper = new QRCodeHelper();
        mEncodeTextView = (TextView) findViewById(R.id.tv_encode_url);
        mEncodeButton = (Button) findViewById(R.id.btn_encode);
        mEncodeImageView = (ImageView) findViewById(R.id.iv_encode);

        mEncodeTvLogo = (TextView) findViewById(R.id.tv_encode_logo);
        mEncodeBtnLogo = (Button) findViewById(R.id.btn_encode_logo);
        mEncodeIvLogo = (ImageView) findViewById(R.id.iv_encode_logo);

        mDecodeImageView = (ImageView) findViewById(R.id.iv_decode);
        mDecodeButton = (Button) findViewById(R.id.btn_decode);
        mDecodeTextView = (TextView) findViewById(R.id.tv_decode_url);

        mEncodeButton.setOnClickListener(this);
        mEncodeBtnLogo.setOnClickListener(this);
        mDecodeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_encode:
                qrEncode();
                break;

            case R.id.btn_encode_logo:
                qrEncodeLogo();
                break;

            case R.id.btn_decode:
                qrDecode();
                break;
        }
    }

    private void qrEncode() {
        String url = mEncodeTextView.getText().toString().trim();
        try {
            Bitmap bitmap = mQrCodeHelper.encodeQRCode(url, 100, 100, 2);
            if (bitmap != null) {
                mEncodeImageView.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void qrEncodeLogo() {
        String url = mEncodeTvLogo.getText().toString().trim();
        try {
            Bitmap bmpLogo = BitmapFactory.decodeResource(getResources(), R.drawable.le);
            Bitmap bitmap = mQrCodeHelper.encodeQRCodeWithLogo(url, 300, 300, 0, 5, bmpLogo);
            if (bitmap != null) {
                mEncodeIvLogo.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void qrDecode() {
        try {
            Drawable drawable = mDecodeImageView.getDrawable();
            String url = mQrCodeHelper.decodeQRCode(((BitmapDrawable) drawable).getBitmap());
            mDecodeTextView.setText(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
