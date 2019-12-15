package com.example.intelrobot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

class ChatAdapter  extends BaseAdapter {
    private List<ChatBean> chatBeanList;
    private LayoutInflater layoutInflater;

    public ChatAdapter(List<ChatBean> chatBeanList, Context context) {
        this.chatBeanList = chatBeanList;
        layoutInflater = layoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return chatBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup viewGroup) {
        Holder holder = new Holder();
        if(chatBeanList.get(position).getState() == ChatBean.RECEIVE){
            contentView = layoutInflater.inflate(R.layout.chatting_left_item, null);
        } else {
            contentView = layoutInflater.inflate(R.layout.chatting_right_item, null);
        }
        holder.tv_chat_content = contentView.findViewById(R.id.tv_chat_content);
        holder.tv_chat_content.setText(chatBeanList.get(position).getMessage());
        return contentView;
    }
    class Holder{
        public TextView tv_chat_content;
    }
}
