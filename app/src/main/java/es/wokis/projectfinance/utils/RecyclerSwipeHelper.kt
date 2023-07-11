package es.wokis.projectfinance.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class RecyclerSwipeHelper(
    private val leftButton: SwipeButton,
    private val rightButton: SwipeButton
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    private var intrinsicWidth: Int = leftButton.drawable?.intrinsicWidth.orZero()
    private var intrinsicHeight: Int = leftButton.drawable?.intrinsicHeight.orZero()
    private var clearPaint: Paint = Paint()
    private val background = ColorDrawable()
    private var recycler: RecyclerView? = null

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.LEFT) {
            leftButton.event(viewHolder.adapterPosition)

        } else {
            rightButton.event(viewHolder.adapterPosition)
        }
        recycler?.adapter?.notifyItemChanged(viewHolder.adapterPosition)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView: View = viewHolder.itemView
        val itemHeight: Int = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive
        val itemTop: Int = itemView.top + (itemHeight - intrinsicHeight) / 2
        val itemMargin = (itemHeight - intrinsicHeight) / 2
        val itemBottom = itemTop + intrinsicHeight
        recycler = recyclerView
        if (isCanceled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            leftButton.drawable?.alpha = MAX_ALPHA
            rightButton.drawable?.alpha = MAX_ALPHA
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, false)
            return
        }

        if (dX < 0) {
            val itemLeft: Int = itemView.right - itemMargin - intrinsicWidth
            val itemRight: Int = itemView.right - itemMargin
            var alpha = (-itemView.translationX / itemView.width * SWIPE_ALPHA_LIMIT).toInt()
            if (alpha > MAX_ALPHA) alpha = MAX_ALPHA
            background.color = leftButton.color
            background.setBounds(
                (itemView.right + dX).toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            background.draw(c)
            leftButton.drawable?.let {
                it.setTint(getTintByContrast(leftButton.color))
                it.alpha = alpha
                it.setBounds(itemLeft, itemTop, itemRight, itemBottom)
                it.draw(c)
            }

        } else {
            val itemLeft: Int = itemView.left + itemMargin
            val itemRight: Int = itemView.left + itemMargin + intrinsicWidth
            var alpha = (itemView.translationX / itemView.width * SWIPE_ALPHA_LIMIT).toInt()
            if (alpha > MAX_ALPHA) alpha = MAX_ALPHA
            background.color = rightButton.color
            background.setBounds(
                (itemView.left + dX).toInt(),
                itemView.top,
                itemView.left,
                itemView.bottom
            )
            background.draw(c)
            rightButton.drawable?.let {
                it.setTint(getTintByContrast(leftButton.color))
                it.alpha = alpha
                it.setBounds(itemLeft, itemTop, itemRight, itemBottom)
                it.draw(c)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (TAG_NO_SWIPE == viewHolder.itemView.tag) {
            NO_SWIPE

        } else {
            super.getSwipeDirs(
                recyclerView,
                viewHolder
            )
        }
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }

    companion object {
        const val TAG_NO_SWIPE = "please porfaplis no me suipees"
        private const val NO_SWIPE = 0
        private const val SWIPE_ALPHA_LIMIT = 620
        private const val MAX_ALPHA = 255
    }

    init {
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
}