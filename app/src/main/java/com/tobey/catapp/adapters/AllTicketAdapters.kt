package com.tobey.catapp.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tobey.catapp.R
import com.tobey.catapp.TicketActivity
import com.tobey.catapp.models.Ticket

class AllTicketAdapters(val tickets: ArrayList<Ticket>) :
    RecyclerView.Adapter<AllTicketAdapters.ViewHolder>(){

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val cont = view.context

        private val ticketCard: CardView = view.findViewById(R.id.ticket_card)
//        private val ownerName: TextView = view.findViewById(R.id.owner_name)
        private val startingFrom: TextView = view.findViewById(R.id.starting_from)
        private val destination: TextView = view.findViewById(R.id.destination)
        private val ticketNumber: TextView = view.findViewById(R.id.ticket_number)
        private var currentTicket: Ticket? = null

        init {
            view.setOnClickListener {
                currentTicket?.let { }
            }
        }


        fun bind(ticket: Ticket) {
            currentTicket = ticket
//            ownerName.text = ticket.name
            startingFrom.text = ticket.source
            destination.text = ticket.destination
            ticketNumber.text = ticket.ticket_no

            ticketCard.setOnClickListener {

                val toTicket = Intent(cont, TicketActivity::class.java)
                toTicket.putExtra("source", ticket.source)
                toTicket.putExtra("destination", ticket.destination)
                toTicket.putExtra("ticket_no", ticket.ticket_no)
                toTicket.putExtra("name", ticket.name)
                toTicket.putExtra("date", ticket.date)
                toTicket.putExtra("time", ticket.time)
                toTicket.putExtra("image", ticket.image)
                toTicket.putExtra("record_id", ticket.record_id)

                cont.startActivity(toTicket)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ticket_row, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tickets.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.bind(ticket)
    }
}