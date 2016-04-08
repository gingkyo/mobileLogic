package com.wglxy.example.dragdrop;

/*
 * Copyright (C) 2013 Wglxy.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * (Note to other developers: The above note says you are free to do what you want with this code.
 *  Any problems are yours to fix. Wglxy.com is simply helping you get started. )
 */

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

//import android.content.ClipData;
//import android.content.ClipDescription;

/**
 * This activity presents a screen with a grid on which images can be added and moved around.
 * It also defines areas on the screen where the dragged views can be dropped. Visual feedback is
 * provided to the user as the objects are dragged over the views where something can be dropped.
 * <p/>
 * <p/>
 * This example starts dragging when a DragSource view is touched.
 * If you want to start with a long press (long click), set the variable mLongClickStartsDrag to true.
 */

public class DragActivity extends Activity 
    implements View.OnClickListener,
               View.OnTouchListener {

    public static final String LOG_NAME = "DragActivity";

    /**
     */
// Variables

    private DragController mDragController;   // Object that handles a drag-drop sequence.
    // It interacts with DragSource and DropTarget objects.
    private ImageCell mLastNewCell = null;    // The last ImageCell added to the screen when Add Image is clicked.
    private ImageView powerButton_1,powerButton_2,powerButton_3;
    private boolean addWireMode=false;

    public static final boolean Debugging = false;   // Use this to see extra toast messages while debugging.
/**
 */
// Methods

    /**
     * Add a new image so the user can move it around. It shows up in the image_source_frame
     * part of the screen.
     *
     * @param resourceId int - the resource id of the image to be added
     */

    public void addNewImageToScreen(int resourceId) {
        if (mLastNewCell != null) mLastNewCell.setVisibility(View.GONE);

        FrameLayout imageHolder = (FrameLayout) findViewById(R.id.image_source_frame);
        if (imageHolder != null) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT,
                    Gravity.CENTER);
            ImageCell newView = new ImageCell(this);
            newView.setGate(logicGateFactory(resourceId));
            newView.setImageResource(resourceId);
            imageHolder.addView(newView, lp);
            newView.mEmpty = false;
            newView.mCellNumber = -1;
            mLastNewCell = newView;

            // Have this activity listen to touch and click events for the view.
            newView.setOnClickListener(this);
            //newView.setOnLongClickListener(this);
            newView.setOnTouchListener(this);

        }
    }

    public Gate logicGateFactory(int resourceId) {
        switch (resourceId) {
            case R.drawable.and_gate:
                return new AndGate();
            case R.drawable.nand_gate:
                return new NandGate();
            case R.drawable.nor_gate:
                return new NorGate();
            case R.drawable.not_gate:
                return new NotGate();
            case R.drawable.or_gate:
                return new OrGate();
            case R.drawable.xnor_gate:
                return new XnorGate();
            case R.drawable.xor_gate:
                return new XorGate();
        }
        return null;
    }

    /**
     * Add one of the images to the screen so the user has a new image to move around.
     * See addImageToScreen.
     */

    public void addNewImageToScreen() {
//    int resourceId = R.drawable.hello;
//
//    int m = mImageCount % 3;
//    if (m == 1) resourceId = R.drawable.photo1;
//    else if (m == 2) resourceId = R.drawable.photo2;
//    addNewImageToScreen (resourceId);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add_wire:
                addWireMode=true;
                break;
            case R.id.button_select_gate:
                addNewImageToScreen(R.drawable.and_gate);
                break;
            case R.id.button_undo_gate:
                break;
            case R.id.imageView_power_1 :
                setPowerButton(powerButton_1);
                break;
            case R.id.imageView_power_2 :
                setPowerButton(powerButton_2);
                break;
            case R.id.imageView_power_3 :
                setPowerButton(powerButton_3);
                break;
            default:
                toast("Button Event Error");
        }
    }

    /**
     * onCreate - called when the activity is first created.
     * <p/>
     * Creates a drag controller and sets up three views so click and long click on the views are sent to this activity.
     * The onLongClick method starts a drag sequence.
     */

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circuit_screen);

        // When a drag starts, we vibrate so the user gets some feedback.

        // This activity will listen for drag-drop events.
        // The listener used is a DragController. Set it up.
        mDragController = new DragController();

        // Set up the grid view with an ImageCellAdapter and have it use the DragController.
        GridView gridView = (GridView) findViewById(R.id.image_grid_view);
        powerButton_1 =(ImageView)findViewById(R.id.imageView_power_1);
        powerButton_1.setOnClickListener(this);
        powerButton_2 =(ImageView)findViewById(R.id.imageView_power_2);
        powerButton_2.setOnClickListener(this);
        powerButton_3 =(ImageView)findViewById(R.id.imageView_power_3);
        powerButton_3.setOnClickListener(this);

        if (gridView == null) toast("Unable to find GridView");
        else {
            gridView.setAdapter(new ImageCellAdapter(this, mDragController));
        }

    }
    /**
     * This is the starting point for a drag operation if mLongClickStartsDrag is false.
     * It looks for the down event that gets generated when a user touches the screen.
     * Only that initiates the drag-drop sequence.
     */

    public boolean onTouch(View v, MotionEvent ev) {
        // If we are configured to start only on a long click, we are not going to handle any events here.
        boolean handledHere = false;

        final int action = ev.getAction();

        // In the situation where a long click is not needed to initiate a drag, simply start on the down event.
        if (action == MotionEvent.ACTION_DOWN) {
            handledHere = startDrag(v);
        }

        return handledHere;
    }
    public void setPowerButton(ImageView powerButton){
        String tag=powerButton.getTag().toString();
        if(tag.equals("power_off")){
          powerButton.setImageResource(R.drawable.power_on);
          powerButton.setTag("power_on");
        }
        if(tag.equals("power_on")){
            powerButton.setImageResource(R.drawable.power_off);
            powerButton.setTag("power_off");
        }
        toast(tag);
    }

    /**
     * Start dragging a view.
     */
    public boolean startDrag(View v) {
        // We are starting a drag-drop operation.
        // Set up the view and let our controller handle it.
        v.setOnDragListener(mDragController);
        mDragController.startDrag(v);
        return true;
    }

    /**
     * Show a string on the screen via Toast.
     *
     * @param msg String
     */

    public void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    } // end toast

    /**
     * Send a message to the debug log. Also display it using Toast if Debugging is true.
     */

    public void trace(String msg) {
        Log.d(LOG_NAME, msg);
        if (Debugging) toast(msg);
    }

}