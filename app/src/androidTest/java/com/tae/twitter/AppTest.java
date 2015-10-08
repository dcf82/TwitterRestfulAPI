package com.tae.twitter;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tae.twitter.screens.UserActivity;

/**
 * Testing for User Home Activity Class
 *
 * @author David Castillo Fuentes <dcaf82@gmail.com>
 *
 */
public class AppTest extends ActivityInstrumentationTestCase2<UserActivity> {

    private UserActivity mUserActivity;
    private TextView mSendTweetButton;
    private EditText mMessage;

    public AppTest() {
        super(UserActivity.class);
    }

    /**
     * Sets up the test fixture for this test case. This method is always called before every test
     * run.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //Sets the initial touch mode for the Activity under test. This must be called before
        //getActivity()
        setActivityInitialTouchMode(true);

        //Get a reference to the Activity under test, starting it if necessary.
        mUserActivity = getActivity();

        //Get references to all views
        mSendTweetButton = (TextView) mUserActivity.findViewById(R.id.sendMsg);
        mMessage = (EditText) mUserActivity.findViewById(R.id.msg);
    }

    /**
     * Tests the preconditions of this test fixture.
     */
    @MediumTest
    public void testPreconditions() {
        assertNotNull("mUserActivity is null", mUserActivity);
        assertNotNull("mSendTweetButton is null", mSendTweetButton);
        assertNotNull("mMessage is null", mMessage);
    }

    @MediumTest
    public void testSendButtonMessage() {
        // Verify that mSendTweetButton uses the correct string resource
        final String expectedNextButtonText = mUserActivity.getString(R.string.tweet);
        final String actualNextButtonText = mSendTweetButton.getText().toString();
        assertEquals(expectedNextButtonText, actualNextButtonText);
    }

    @MediumTest
    public void testMessageBox() {
        //Retrieve the top-level window decor view
        final View decorView = mUserActivity.getWindow().getDecorView();

        //Verify that the mMessage is on screen and is not visible
        ViewAsserts.assertOnScreen(decorView, mMessage);
        assertTrue(View.VISIBLE == mMessage.getVisibility());
    }

    @MediumTest
    public void testClickSendTweet() {
        String expectedInfoText = mUserActivity.getString(R.string.tweet);

        mUserActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessage.setText("Hey, I'm in twitter");
            }
        });

        // Perform a click on mSendTweetButton
        TouchUtils.clickView(this, mSendTweetButton);

        // Verify the that mSendTweetButton was clicked. mMessage is visible and contains
        // the correct text.
        assertTrue(View.VISIBLE == mMessage.getVisibility());
        assertNotSame("", mMessage.getText());
    }
}
