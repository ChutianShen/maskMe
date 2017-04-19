package edu.ucsb.ece150.maskme;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

//import com.google.android.gms.vision.face.FaceDetector;

public class MainActivity extends AppCompatActivity {
    public enum Mode {
        PREVIEW,
        CAPTURE,
        MASK
    }

    public static final int MAX_FACES = 2;

    private MaskCameraSurfaceView mCameraSurface;
    private MaskedImageView mImageView;
    private MaskedImageView mCopiedImageView;

    private FrameLayout mCameraFrame;
    private Button mCameraButton;
    private Bitmap mImage;
    private Bitmap mCopiedImage;

    private Mode mMode = Mode.PREVIEW;

    private Button lucky;
    private Button change;

//    public FaceDetector mFaceDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lucky = (Button) findViewById(R.id.lucky);
        change = (Button) findViewById(R.id.change);
        lucky.setVisibility(View.GONE);
        change.setVisibility(View.GONE);
        setupCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCameraSurface.stopCamera();
    }

    private void setupCamera() {
        /* your code here:
            - create the new masked camera surface view with application context
            - create the new masked image view with application context
            - set masked image view scale type to FIT_XY
            - get the camera frame from the resource R.id.cameraDisplay
            - get the camera button from R.id.cameraButton
            - set the button on click method to be our cameraButtonOnClick() method
            - add the new masked camera surface and masked image view to camera frame
            - bring the camera surface view to front
        */

        mCameraSurface = new MaskCameraSurfaceView(getApplicationContext());
        mImageView = new MaskedImageView(getApplicationContext());
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);

        mCopiedImageView = new MaskedImageView(getApplicationContext());
        mCopiedImageView.setScaleType(ImageView.ScaleType.FIT_XY);

        mCameraFrame = (FrameLayout) findViewById(R.id.cameraDisplay);
        mCameraButton = (Button) findViewById(R.id.cameraButton);

        // I add "cameraButtonOnClick()" in the onClick in the activity_main.xml directly

        mCameraFrame.addView(mCameraSurface);
        mCameraFrame.addView(mImageView);
        mCameraSurface.bringToFront();

    }



    private Bitmap rotateImage(Bitmap image) {      //used in takePicture function
        final Matrix matrix = new Matrix();

        matrix.postTranslate(0f - image.getWidth()/2, 0f - image.getHeight()/2);
        matrix.postRotate(mCameraSurface.getCurrentRotation());
        matrix.postTranslate(image.getWidth() / 2, image.getHeight() / 2);
        return Bitmap.createBitmap(image,0, 0, image.getWidth(), image.getHeight(), matrix, false);
    }

    /*********************************************************************************************/
    //three methods implemented by mCameraButton

    private void takePicture() {
        mCameraSurface.capture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                final Bitmap snapShot = rotateImage(BitmapFactory.decodeByteArray(data, 0, data.length));
                mImage = snapShot.copy(Bitmap.Config.RGB_565, false);
                mImageView.setImageBitmap(mImage);
                mImageView.invalidate();
                mCameraFrame.bringChildToFront(mImageView);

                mCopiedImage = snapShot.copy(Bitmap.Config.RGB_565, false);
                mCopiedImageView.setImageBitmap(mCopiedImage);
            }
        });
    }

    private FaceDetector.Face[] captureFaces(Bitmap inputImage){
        FaceDetector faceDetector = new FaceDetector(inputImage.getWidth(), inputImage.getHeight(), MAX_FACES);

        FaceDetector.Face[] faces = new FaceDetector.Face[MAX_FACES];

        int numOfFaces = faceDetector.findFaces(inputImage, faces);
        Log.i("NumberOfFaces", String.valueOf(numOfFaces));

        return faces;
    }

    private void addMask(FaceDetector.Face[] faces) {
        /* your code here:
            - make a new detector passing it mImage's width and height and the MAX_FACES variable
            - initialize an array of FaceDetector.Face objects the size of MAX_FACES
            - run facial detection with the "find faces" method
            - if there are some faces, call the maskFaces() method of the surface view
            - if not, call noFaces()
            - invalidate the surface view to refresh the drawing
        */

        //move below to the captureFaces function
        /*
        FaceDetector faceDetector = new FaceDetector(mImage.getWidth(), mImage.getHeight(), MAX_FACES);

        FaceDetector.Face[] faces = new FaceDetector.Face[MAX_FACES];

        int numOfFaces = faceDetector.findFaces(mImage, faces);
        Log.i("NumberOfFaces", String.valueOf(numOfFaces));
        */

        int numOfFaces = faces.length;

        if(numOfFaces > 0){
            mImageView.maskFaces(faces, numOfFaces, mImage.getWidth(), mImage.getHeight());
            mImageView.invalidate();
            //mCameraSurface.maskFaces();
        } else{
            mImageView.noFaces();
        }
        //mImageView.invalidate();
        mCameraSurface.invalidate();
    }

    private void goToChange(FaceDetector.Face[] faces){
        int numOfFaces = faces.length;

        if(numOfFaces > 0){
            mImageView.maskFaces(faces, numOfFaces, mImage.getWidth(), mImage.getHeight());
            mImageView.invalidate();
            //mCameraSurface.maskFaces();
        } else{
            mImageView.noFaces();
        }
        //mImageView.invalidate();
        mCameraSurface.invalidate();
    }

    private void maskOnce(FaceDetector.Face[] faces){
        int numOfFaces = faces.length;

        if(numOfFaces > 0){
            mImageView.maskFaces(faces, numOfFaces, mImage.getWidth(), mImage.getHeight());
            mImageView.invalidate();
            //mCameraSurface.maskFaces();
        } else{
            mImageView.noFaces();
        }
        //mImageView.invalidate();
        mCameraSurface.invalidate();
    }

    private void resetCamera() {
        mCameraFrame.bringChildToFront(mCameraSurface);
        mImageView.reset();
        mCameraSurface.startPreview();
    }

    /*********************************************************************************************/

    public void cameraButtonOnClick(View v) {

        //mMode = Mode.CAPTURE;

        Log.i("Current Mode ", mMode.toString());

        switch (mMode) {
            case PREVIEW:
                takePicture();
                mCameraButton.setText(getString(R.string.add_mask));
                mMode = Mode.CAPTURE;
                break;
            case CAPTURE:
                //FaceDetector.Face[] faces = captureFaces(mImage);
                mImageView.mode = 2;
                maskOnce(captureFaces(mImage));
                lucky = (Button) findViewById(R.id.lucky);
                lucky.setVisibility(View.VISIBLE);
                mCameraButton.setText(getString(R.string.show_preview));
                mMode = Mode.MASK;
                break;
            case MASK:
                lucky = (Button) findViewById(R.id.lucky);
                change = (Button) findViewById(R.id.change);
                lucky.setVisibility(View.GONE);
                change.setVisibility(View.GONE);
                resetCamera();
                mCameraButton.setText(getString(R.string.take_picture));
                mMode = Mode.PREVIEW;
                break;
            default:
                break;
        }
    }

    public void lucky(View v){
        mImageView.mode = 0;
        addMask(captureFaces(mImage));
        change = (Button) findViewById(R.id.change);
        change.setVisibility(View.VISIBLE);
    }

    public void changeMask(View v){
        mImageView.mode = 1;
        goToChange(captureFaces(mImage));
    }
}
