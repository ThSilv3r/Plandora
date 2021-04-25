package com.plandora.controllers

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.plandora.activity.CreateEventActivity
import com.plandora.activity.PlandoraActivity
import com.plandora.models.events.Event
import com.plandora.utils.constants.FirestoreConstants
import kotlin.collections.ArrayList

class PlandoraEventController {

    companion object {
        val eventList: ArrayList<Event> = ArrayList()
    }

    private val firestoreInstance = FirebaseFirestore.getInstance()

    fun createEvent(activity: CreateEventActivity, event: Event) {
        // Add a new document with a generated id.
        firestoreInstance.collection(FirestoreConstants.EVENTS)
                .add(event)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                    activity.onSuccess()
                    eventList.add(event)
                    //Hier wird User/Ersteller zum Topic hinzugefügt, Attendees fehlen aber noch
                    FirebaseMessaging.getInstance().subscribeToTopic(documentReference.id)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    activity.onFailure()
                }
    }

    fun getEventList(activity: PlandoraActivity) {
        val currentTimestamp = System.currentTimeMillis() - 8.64e7
        firestoreInstance.collection(FirestoreConstants.EVENTS)
            .whereEqualTo(FirestoreConstants.EVENT_OWNER_ID, PlandoraUserController().currentUserId())
            .get()
            .addOnSuccessListener { document ->
                eventList.clear()
                for(i in document.documents) {
                    val event = i.toObject(Event::class.java)!!
                    if(event.annual || event.timestamp > currentTimestamp) {
                        eventList.add(event)
                    }
                }
                eventList.sort()
            }
            .addOnFailureListener {
                Toast.makeText(activity.baseContext, it.message, Toast.LENGTH_SHORT).show()
            }
    }

}