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
import com.alfaloop.android.alfabridge.db.model.DeployRecord;
import com.alfaloop.android.alfabridge.listener.OnSwipeViewClickListener;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 2017/9/2.
 */

public class DeployRecordAdapter extends RecyclerSwipeAdapter<RecyclerView.ViewHolder> {
    private static final String TAG = DeployRecordAdapter.class.getSimpleName();

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_ITEM = 2;

    private List<DeployRecord> mItems = new ArrayList<>();

    private LayoutInflater mInflater;

    private OnSwipeViewClickListener mSwipeViewClickListener;
    private Context mContext;

    public DeployRecordAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    public void setDatas(List<DeployRecord> items) {
        mItems.clear();
        mItems.addAll(items);
    }

    public void setOnSwipeViewClickListener(OnSwipeViewClickListener clickListener) {
        this.mSwipeViewClickListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SwipeLayout view = (SwipeLayout) mInflater.inflate(R.layout.item_deploy_record, parent, false);
        if (viewType == TYPE_ITEM) {
            ItemViewHolder holder = new ItemViewHolder(view);
            return holder;
        } else if (viewType == TYPE_HEADER) {
            //Inflating header view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(itemView);
        } else return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {
            Log.i(TAG, "onBindViewHolder HeaderViewHolder");
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.headerTitle.setText(R.string.deploy_reocrd_list_title);
         } else if (holder instanceof ItemViewHolder) {
            // TODO:  update view content
            ItemViewHolder itemHolder = (ItemViewHolder)holder;
            final int idx = position - 1;
            DeployRecord item = mItems.get(idx);
            itemHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            itemHolder.swipeLayout.findViewById(R.id.trash).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Log.i(TAG, String.format("Delete click: position %d", idx));
                    if (mSwipeViewClickListener != null) {
                        mSwipeViewClickListener.onSwipeDeleteClick(idx, v);
                    }
                }
            });

            itemHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, String.format("Surface click: position %d", idx));
                    if (mSwipeViewClickListener != null) {
                        mSwipeViewClickListener.onSurfaceClick(idx, v);
                    }
                }
            });

            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy  hh:mm:ss a");

            itemHolder.tvName.setText(item.getName());

            String date = formatter.format(item.getUpdateDate());
            itemHolder.tvDate.setText(date);
            itemHolder.tvContent.setText(String.format("type %s uuid:%s", item.getType(), item.getUuid()));
            itemHolder.tvVersion.setText(item.getVersion());

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            int color = generator.getColor(item.getName());

            // declare the builder object once.
            TextDrawable.IBuilder builder = TextDrawable.builder()
                    .beginConfig()
                    .withBorder(4)
                    .endConfig()
                    .round();

            // reuse the builder specs to create multiple drawables
            TextDrawable drawable = builder.build(item.getName().substring(0, 1), color);
            itemHolder.imvAvatar.setImageDrawable(drawable);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mItems.size() + 1;
    }

    public DeployRecord getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitle;
        public HeaderViewHolder(View view) {
            super(view);
            headerTitle = (TextView) view.findViewById(R.id.header_text);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView footerText;
        public FooterViewHolder(View view) {
            super(view);
            footerText = (TextView) view.findViewById(R.id.footer_text);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private SwipeLayout swipeLayout;
        private TextView tvName;
        private TextView tvContent;
        private TextView tvDate;
        private TextView tvVersion;
        private ImageView imvAvatar;

        public ItemViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            imvAvatar = (ImageView) itemView.findViewById(R.id.list_image);
            tvName = (TextView) itemView.findViewById(R.id.name);
            tvContent = (TextView) itemView.findViewById(R.id.content);
            tvVersion = (TextView) itemView.findViewById(R.id.version);
            tvDate=  (TextView) itemView.findViewById(R.id.date);
        }
    }
}
