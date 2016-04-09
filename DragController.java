package com.wglxy.example.dragdrop;

import android.content.ClipData;
import android.view.DragEvent;
import android.view.View;

public class DragController
        implements View.OnDragListener
{
    private DragSource mDragSource;

    public DragController(){

    }
    @Override public boolean onDrag (View v, DragEvent event) {

        boolean isDragSource = false, isDropTarget = false;
        DragSource source = null;
        DropTarget target = null;
        try {
            source = (DragSource) v;
            isDragSource = true;
        } catch (ClassCastException ex) {}
        try {
            target = (DropTarget) v;
            isDropTarget = true;
        } catch (ClassCastException ex) {}

        boolean eventResult = false;
        final int action = event.getAction();

        switch(action) {

            case DragEvent.ACTION_DRAG_STARTED:

                if (isDragSource) {
                    if (source == mDragSource) {
                        if (source.allowDrag ()) {
                            eventResult = true;
                        }
                    } else {
                        eventResult = isDropTarget && target.allowDrop (mDragSource);
                    }
                } else {
                    eventResult = isDropTarget && target.allowDrop(mDragSource);
                }
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                if (isDropTarget) {
                    target.onDragEnter (mDragSource);
                    eventResult = true;
                } else eventResult = false;
                break;

            case DragEvent.ACTION_DRAG_EXITED:
                if (isDropTarget) {
                    target.onDragExit (mDragSource);
                    eventResult = true;
                } else eventResult = false;
                break;

            case DragEvent.ACTION_DROP:
                if (isDropTarget) {
                    if (target.allowDrop (mDragSource)) {
                        target.onDrop (mDragSource);
                    }
                    eventResult = true;
                } else eventResult = false;
                break;

            case DragEvent.ACTION_DRAG_ENDED:

                break;
        }
        return eventResult;
    }
    public boolean startDrag (View v) {

        boolean isDragSource = false;
        DragSource ds = null;
        try {
            ds = (DragSource) v;
            isDragSource = true;
        } catch (ClassCastException ex) {
        }
        if (!isDragSource) return false;
        if (!ds.allowDrag ()) return false;

        mDragSource = ds;

        ClipData dragData = ClipData.newPlainText("","");
        View.DragShadowBuilder shadowView = new View.DragShadowBuilder (v);
        v.startDrag (dragData, shadowView, ds, 0);
        return true;
    }
}
