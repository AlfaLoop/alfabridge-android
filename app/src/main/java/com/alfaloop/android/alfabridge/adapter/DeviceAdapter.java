package com.alfaloop.android.alfabridge.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfaloop.android.alfabridge.R;
import com.alfaloop.android.alfabridge.db.model.ConnectionRecord;
import com.alfaloop.android.alfabridge.listener.OnSwipeViewClickListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends RecyclerSwipeAdapter<DeviceAdapter.MyViewHolder> {
    private static final String TAG = DeviceAdapter.class.getSimpleName();

    private List<ConnectionRecord> mItems = new ArrayList<>();

    private LayoutInflater mInflater;

    private OnSwipeViewClickListener mSwipeViewClickListener;
    private Context mContext;

    public DeviceAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    public void setDatas(List<ConnectionRecord> items) {
        mItems.clear();
        mItems.addAll(items);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SwipeLayout view = (SwipeLayout) mInflater.inflate(R.layout.item_device, parent, false);
        final MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // TODO:  update view content
        ConnectionRecord item = mItems.get(position);

        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.swipeLayout.findViewById(R.id.trash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // issue the confirm dialog
                Log.i(TAG, String.format("Delete click: position %d", position));
                if (mSwipeViewClickListener != null) {
                    mSwipeViewClickListener.onSwipeDeleteClick(position, v);
                }
            }
        });

        holder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, String.format("Surface click: position %d", position));
                if (mSwipeViewClickListener != null) {
                    mSwipeViewClickListener.onSurfaceClick(position, v);
                }
            }
        });

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy  hh:mm:ss a");

        String macAddr = String.format("%s:%s:%s:%s:%s:%s", item.getMacAddr().substring(0 , 2),
                 item.getMacAddr().substring(2 , 4),
                item.getMacAddr().substring(4 , 6),
                item.getMacAddr().substring(6 , 8),
                item.getMacAddr().substring(8 , 10),
                item.getMacAddr().substring(10 , 12));

        holder.tvName.setText(macAddr);

        String date = formatter.format(item.getUpdateDate());
        holder.tvDate.setText(date);
        holder.tvVersion.setText("");
        holder.imvAvatar.setImageResource(R.drawable.ic_shortcut_memory_block);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public ConnectionRecord getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private SwipeLayout swipeLayout;
        private TextView tvName;
        private TextView tvPlatform;
        private TextView tvDate;
        private TextView tvVersion;
        private ImageView imvAvatar;

        public MyViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            imvAvatar = (ImageView) itemView.findViewById(R.id.list_image);
            tvName = (TextView) itemView.findViewById(R.id.name);
            tvPlatform = (TextView) itemView.findViewById(R.id.platform);
            tvVersion = (TextView) itemView.findViewById(R.id.version);
            tvDate=  (TextView) itemView.findViewById(R.id.date);
        }
    }

    public void setOnSwipeViewClickListener(OnSwipeViewClickListener clickListener) {
        this.mSwipeViewClickListener = clickListener;
    }
}
