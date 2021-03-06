package com.plandora.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.plandora.R
import com.plandora.models.events.Event
import com.plandora.models.PlandoraUser
import kotlinx.android.synthetic.main.layout_attendees_list_item.view.*

class AttendeeRecyclerAdapter(private var event: Event, private var items: List<PlandoraUser>, private val onDeleteButtonListener: OnDeleteButtonListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AttendeeViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.layout_attendees_list_item, parent, false), onDeleteButtonListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is AttendeeViewHolder -> {
                holder.bind(event, items[position])
            }
        }
    }

    class AttendeeViewHolder(itemView: View, private val onClickListener: OnDeleteButtonListener) : RecyclerView.ViewHolder(itemView), OnDeleteButtonListener {
        private val displayName: TextView = itemView.attendee_displayName
        private val eventOwnerIcon: ImageView = itemView.attendee_owner_icon
        private val eventDeleteButton: ImageView = itemView.attendee_delete_button

        fun bind(event: Event, user: PlandoraUser) {
            displayName.text = user.displayName
            if(event.isOwner(user)) {
                eventOwnerIcon.setImageResource(R.drawable.ic_event_owner_icon)
                eventDeleteButton.visibility
            } else if(event.isInvitedUser(user)) {
                eventDeleteButton.setImageResource(R.drawable.ic_event_attendee_pending)
            } else {
                eventDeleteButton.setImageResource(R.drawable.ic_remove_attendee_icon)
                eventDeleteButton.setOnClickListener{ onDeleteAttendeeButtonClicked(adapterPosition) }
            }
        }

        override fun onDeleteAttendeeButtonClicked(position: Int) {
            onClickListener.onDeleteAttendeeButtonClicked(adapterPosition)
        }

    }

    interface OnDeleteButtonListener {
        fun onDeleteAttendeeButtonClicked(position: Int)
    }

}