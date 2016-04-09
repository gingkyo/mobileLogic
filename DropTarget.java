package com.wglxy.example.dragdrop;

public interface DropTarget {

boolean allowDrop (DragSource source);

void onDrop (DragSource source);

void onDragEnter (DragSource source);

void onDragExit (DragSource source);

void setGate(Gate gate);
}

