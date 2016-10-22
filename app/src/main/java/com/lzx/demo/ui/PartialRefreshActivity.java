package com.lzx.demo.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.lzx.demo.R;
import com.lzx.demo.base.ListBaseAdapter;
import com.lzx.demo.bean.ItemModel;
import com.lzx.demo.util.TLog;
import com.lzx.demo.view.SampleFooter;
import com.lzx.demo.view.SampleHeader;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 局部刷新
 */
public class PartialRefreshActivity extends AppCompatActivity {

    private LRecyclerView mRecyclerView = null;

    private DataAdapter mDataAdapter = null;

    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_ll_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (LRecyclerView) findViewById(R.id.list);

        //init data
        ArrayList<ItemModel> dataList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ItemModel itemModel = new ItemModel();
            itemModel.title = "item" + i;
            itemModel.imgUrl = "http://img.blog.csdn.net/20160603160856217";
            dataList.add(itemModel);
        }

        mDataAdapter = new DataAdapter(this);
        mDataAdapter.setDataList(dataList);

        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mDataAdapter);
        mRecyclerView.setAdapter(mLRecyclerViewAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //add a HeaderView
        View header = LayoutInflater.from(this).inflate(R.layout.sample_header,(ViewGroup)findViewById(android.R.id.content), false);

        mLRecyclerViewAdapter.addHeaderView(header);
        mLRecyclerViewAdapter.addHeaderView(new SampleHeader(this));

        //add a FooterView
        mLRecyclerViewAdapter.addFooterView(new SampleFooter(this));

        //禁止下拉刷新功能
        mRecyclerView.setPullRefreshEnabled(false);

    }

    private class DataAdapter extends ListBaseAdapter<ItemModel> {

        private LayoutInflater mLayoutInflater;

        public DataAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            return new ViewHolder(mLayoutInflater.inflate(R.layout.list_item_pic, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLog.error("onBindViewHolder 00000");
            ItemModel itemModel = mDataList.get(position);

            ViewHolder viewHolder = (ViewHolder) holder;

            viewHolder.textView.setText(itemModel.title);
            Glide.with(mContext)
                    .load(itemModel.imgUrl)
                    .crossFade()
                    .placeholder(R.mipmap.icon)
                    .into(viewHolder.avatarImage);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
            TLog.error("onBindViewHolder payloads.isEmpty() ?  " + payloads.isEmpty());
            if (payloads.isEmpty()) {
                onBindViewHolder(holder,position);
            } else {//更新想更新的控件
                ItemModel itemModel = mDataList.get(position);

                ViewHolder viewHolder = (ViewHolder) holder;

                viewHolder.textView.setText(itemModel.title);

                Glide.with(mContext)
                        .load(itemModel.imgUrl)
                        .placeholder(R.mipmap.icon)
                        .into(viewHolder.avatarImage);
            }
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            private TextView textView;
            private ImageView avatarImage;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.info_text);
                avatarImage = (ImageView) itemView.findViewById(R.id.avatar_image);
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_partial_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.menu_partial_refresh) {

            int position = 1;//指定列表中的第2个item
            //改变第4个item值
            ItemModel itemModel = mDataAdapter.getDataList().get(position);
            itemModel.id = 100;
            itemModel.title = "refresh item " + itemModel.id;
            itemModel.imgUrl = "http://avatar.csdn.net/2/9/C/1_jdsjlzx.jpg";
            mDataAdapter.getDataList().set(position,itemModel);

            //RecyclerView局部刷新  详见：https://github.com/jdsjlzx/LRecyclerView/issues/45
            mLRecyclerViewAdapter.notifyItemChanged(mLRecyclerViewAdapter.getAdapterPosition(false,position) ,mDataAdapter.getDataList());

        }
        return true;
    }

}