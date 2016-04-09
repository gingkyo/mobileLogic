package com.wglxy.example.dragdrop;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageCell extends ImageView
        implements DragSource, DropTarget
{
    public boolean mEmpty = true;
    public int mCellNumber = -1;
    public GridView mGrid;
    private Gate cellValue;


    public ImageCell (Context context) {
        super (context);
    }
    public ImageCell (Context context, AttributeSet attrs) {
        super (context, attrs);
    }
    public ImageCell (Context context, AttributeSet attrs, int style) {
        super (context, attrs, style);
    }
    public boolean allowDrag () {
        //drag is limited to only the frame where the image is added and not within the grid
        return mCellNumber<0;
    }
    public boolean allowDrop (DragSource source) {
        if (source == this) return false;

        return mEmpty  && (mCellNumber >= 0);
    }
    public void onDrop (DragSource source) {

        mEmpty = false;
        int bg = mEmpty ? R.color.cell_empty : R.color.cell_filled;
        setBackgroundResource (bg);

        ImageView sourceView = (ImageView) source;
        Drawable d = sourceView.getDrawable ();
        if (d != null) {
            this.setGate(source.getGate());
            this.setImageDrawable (d);
            this.invalidate ();
        }
    }
    public void onDragEnter (DragSource source) {
        if (mCellNumber < 0) return;
        int bg = mEmpty ? R.color.cell_empty_hover : R.color.cell_filled_hover;
        setBackgroundResource (bg);
    }
    public void onDragExit (DragSource source) {
        if (mCellNumber < 0) return;
        int bg = mEmpty ? R.color.cell_empty : R.color.cell_filled;
        setBackgroundResource (bg);
    }
    public void setGate(Gate gate){
        cellValue=gate;
    }
    public Gate getGate()
    {
        return cellValue;
    }

}
