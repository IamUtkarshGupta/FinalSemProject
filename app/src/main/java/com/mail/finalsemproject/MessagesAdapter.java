package com.mail.finalsemproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.mail.finalsemproject.Utils.Utils;
import com.mail.finalsemproject.models.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> implements Filterable {

    private Context mContext;
    private Utils mUtils;
    private List<Message> messageList;
    private List<Message> messageListFiltered;

    private ViewGroup parent;

    class MessageViewHolder extends RecyclerView.ViewHolder {

        LinearLayoutCompat lytItemParent;
        ConstraintLayout lytFromPreviewParent;
        AppCompatTextView txtFromPreview, txtFrom, txtDate, txtSubject, txtSnippet;

        public MessageViewHolder(View itemView) {
            super(itemView);

            lytItemParent = itemView.findViewById(R.id.lytItemParent);
            lytFromPreviewParent = itemView.findViewById(R.id.lytFromPreviewParent);
            txtFromPreview = itemView.findViewById(R.id.txtFromPreview);
            txtFrom = itemView.findViewById(R.id.txtFrom);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtSubject = itemView.findViewById(R.id.txtSubject);
            txtSnippet = itemView.findViewById(R.id.txtSnippet);
        }
    }

    public MessagesAdapter(Context context, List<Message> messageList) {
        this.mContext = context;
        this.mUtils = new Utils(context);
        this.messageList = messageList;
        this.messageListFiltered = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);

        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        final Message message = this.messageListFiltered.get(position);

        holder.lytItemParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUtils.isDeviceOnline()) {
                    Intent intent = new Intent(mContext, EmailActivity.class);
                    intent.putExtra("messageId", message.getId());
                    mContext.startActivity(intent);
                } else
                    mUtils.showSnackbar(parent, mContext.getString(R.string.device_is_offline));
            }
        });

        android.graphics.drawable.GradientDrawable gradientDrawable = (android.graphics.drawable.GradientDrawable) holder.lytFromPreviewParent.getBackground();
        gradientDrawable.setColor(message.getColor());

        holder.txtFromPreview.setText(message.getFrom().substring(0, 1).toUpperCase(Locale.ENGLISH));
        holder.txtFrom.setText(message.getFrom());
        holder.txtDate.setText(mUtils.timestampToDate(message.getTimestamp()));
        holder.txtSubject.setText(message.getSubject());
        holder.txtSnippet.setText(message.getSnippet());
    }

    @Override
    public int getItemCount() {
        return this.messageListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String searchText = constraint.toString().trim().toLowerCase();

                if (searchText.isEmpty()) {
                    messageListFiltered =messageList;
                } else {
                    List<Message> newMessageList = new ArrayList<>();

                    for (Message message :messageList) {
                        if (message.getFrom().toLowerCase().contains(searchText)
                                || message.getSubject().toLowerCase().contains(searchText)
                                || message.getSnippet().toLowerCase().contains(searchText)
                                || mUtils.timestampToDate(message.getTimestamp()).toLowerCase().contains(searchText)
                                )
                            newMessageList.add(message);
                    }

                    messageListFiltered = newMessageList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = messageListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                messageListFiltered = (ArrayList<Message>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}
