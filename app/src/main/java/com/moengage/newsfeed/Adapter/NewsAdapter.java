package com.moengage.newsfeed.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.moengage.newsfeed.Model.News;
import com.moengage.newsfeed.R;


import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "NewsAdapter";
    public static final int VIEW_TYPE_EMPTY = 0;
    public static final int VIEW_TYPE_NORMAL = 1;
    public Callback mCallback;
    private List<News> mNewsList;

    public NewsAdapter(List<News> newsList, Callback callback) {
        mNewsList = newsList;
        mCallback = callback;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
            case VIEW_TYPE_EMPTY:
            default:
                return new EmptyViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_empty_view, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mNewsList != null && mNewsList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mNewsList != null && mNewsList.size() > 0) {
            return mNewsList.size();
        } else {
            return 1;
        }
    }

    public void addItems(List<News> newsList) {
        mNewsList.addAll(newsList);
        notifyDataSetChanged();
    }

    public interface Callback {
        void onEmptyViewRetryClick();
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.thumbnail)
        ImageView coverImageView;
        @BindView(R.id.description)
        TextView descriptionTextView;
        @BindView(R.id.newsTitle)
        TextView newsTitleTextView;
        @BindView(R.id.newsDate)
        TextView newsDateTextView;
        @BindView(R.id.newsAuthor)
        TextView newsAuthorTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        protected void clear() {
            coverImageView.setImageDrawable(null);
            descriptionTextView.setText("");
            newsTitleTextView.setText("");
            newsDateTextView.setText("");
            newsAuthorTextView.setText("");
        }

        public void onBind(int position) {
            super.onBind(position);
            final News mNews = mNewsList.get(position);
            if (mNews.getUrlToImage() != null) {
                Glide.with(itemView.getContext())
                        .load(mNews.getUrlToImage())
                        .into(coverImageView);
            }
            if (mNews.getTitle() != null) {
                newsTitleTextView.setText(mNews.getTitle());
            }
            if (mNews.getDescription() != null) {
                descriptionTextView.setText(mNews.getDescription());
            }
            if (mNews.getPublishedAt() != null) {
                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                SimpleDateFormat output = new SimpleDateFormat("dd'th' MMM yyyy, hh:mm a");

                Date d = null;
                try {
                    d = input.parse(mNews.getPublishedAt());
                    String formatted = output.format(d);
                    Log.i("DATE", "" + formatted);
                    newsDateTextView.setText(formatted);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (mNews.getAuthor() != null && mNews.getAuthor() != "null") {
                newsAuthorTextView.setText("Author: " + mNews.getAuthor());
            } else {
                newsAuthorTextView.setText("Author: NA");

            }
            itemView.setOnClickListener(v -> {
                if (mNews.getUrl() != null) {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse(mNews.getUrl()));
                        itemView.getContext().startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "onClick: Image url is not correct");
                    }
                }
            });
        }
    }

    public class EmptyViewHolder extends BaseViewHolder {
        @BindView(R.id.tv_message)
        TextView messageTextView;
        @BindView(R.id.buttonRetry)
        TextView buttonRetry;

        EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            buttonRetry.setOnClickListener(v -> mCallback.onEmptyViewRetryClick());
        }

        @Override
        protected void clear() {
        }
    }
}