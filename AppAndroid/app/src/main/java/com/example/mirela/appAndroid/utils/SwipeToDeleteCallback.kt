package com.example.mirela.appAndroid.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.widget.Toast
import com.example.mirela.appAndroid.R
import com.example.mirela.appAndroid.chocolate.Chocolate
import com.example.mirela.appAndroid.modelviews.ChocolatesViewModel
import com.example.mirela.appAndroid.syncronize.DeletedItem
import com.example.mirela.appAndroid.syncronize.DeletedItemsDAO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SwipeToDeleteCallback(
    private val context: Context,
    private val adapter: ChocolateAdapter,
    private val chocolateViewModel: ChocolatesViewModel,
    private val deletedItemsDAO: DeletedItemsDAO,
    private val layoutCoordinatorLayout: CoordinatorLayout
) : ItemTouchHelper.Callback() {
    private val mClearPaint: Paint = Paint()
    private val mBackground: ColorDrawable = ColorDrawable()
    private val backgroundColor = Color.parseColor("#960018")
    private val deleteDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_delete_black_24dp)
    private val intrinsicWith = deleteDrawable?.intrinsicWidth
    private val intrinsicHeight = deleteDrawable?.intrinsicHeight

    init {
        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun getMovementFlags(p0: RecyclerView, p1: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT)
    }

    override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, p1: Int) {
        val position = viewHolder.adapterPosition
        val item = adapter.getChocolatesList()[position]

        val sessionManager = SessionManager(context.applicationContext)
        adapter.removeItem(item)
        chocolateViewModel.deleteChocolate(item)
        chocolateViewModel.items.value= adapter.getChocolatesList()

        deleteFromServer(sessionManager.userToken, sessionManager.userId?.toInt(), item)

        val snackbar = Snackbar.make(layoutCoordinatorLayout, "Item was removed", Snackbar.LENGTH_LONG)
        snackbar.setAction("UNDO") {
            adapter.insertItem(item)
            chocolateViewModel.saveChocolate(item)
            chocolateViewModel.items.value= adapter.getChocolatesList()
            insertServer(sessionManager.userToken, sessionManager.userId?.toInt(), item)
        }
        snackbar.setActionTextColor(Color.BLUE)
        snackbar.show()


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
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        val itemHeight = itemView.height
        val isCanceled = dX.toInt() == 0 && !isCurrentlyActive

        if (isCanceled) {
            c.drawRect(
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat(),
                mClearPaint
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        mBackground.color = backgroundColor
        mBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        mBackground.draw(c)
        val deleteIconMargin = (itemHeight - intrinsicHeight!!) / 2

        val deleteIconBottom = itemView.top + intrinsicHeight
        val deleteIconTop = itemView.top + deleteIconMargin

        if (dX < 0) {
            val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWith!!
            val deleteIconRight = itemView.right - deleteIconMargin

            drawIcon(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom, c)
        } else {
            val deleteIconLeft = itemView.left + deleteIconMargin
            val deleteIconRight = itemView.left + deleteIconMargin + intrinsicWith!!

            drawIcon(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom, c)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)


    }

    private fun drawIcon(
        deleteIconLeft: Int,
        deleteIconTop: Int,
        deleteIconRight: Int,
        deleteIconBottom: Int,
        c: Canvas
    ) {
        deleteDrawable?.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteDrawable?.draw(c)
    }

    private fun deleteFromServer(token: String?, userId: Int?, chocolate: Chocolate) {
        chocolateViewModel.deleteChocolateServer(chocolate.id.toInt(), userId)
            .enqueue(object : Callback<Chocolate> {
                override fun onFailure(call: Call<Chocolate>, t: Throwable) {
                    Toast.makeText(
                        context.applicationContext,
                        "Something went wrong or no connection! Delete from local data",
                        Toast.LENGTH_LONG
                    ).show()
                    deletedItemsDAO.insert(DeletedItem(chocolate.id, userId!!.toInt()))
                }

                override fun onResponse(call: Call<Chocolate>, response: Response<Chocolate>) {
//                    Toast.makeText(context.applicationContext, "Delete done", Toast.LENGTH_LONG).show()
                }

            })
    }

    private fun insertServer(token: String?, userId: Int?, chocolate: Chocolate) {
        chocolateViewModel.addChocolateServer(chocolate)
            .enqueue(object : Callback<Chocolate> {
                override fun onFailure(call: Call<Chocolate>, t: Throwable) {
                }

                override fun onResponse(call: Call<Chocolate>, response: Response<Chocolate>) {
                }

            })
    }

}