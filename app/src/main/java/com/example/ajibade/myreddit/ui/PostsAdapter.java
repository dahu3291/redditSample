package com.example.ajibade.myreddit.ui;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ajibade.myreddit.R;
import com.example.ajibade.myreddit.api.NetworkState;
import com.example.ajibade.myreddit.model.Post;

import static com.example.ajibade.myreddit.api.NetworkState.Status.FAILED;
import static com.example.ajibade.myreddit.api.NetworkState.Status.RUNNING;

public class PostsAdapter extends PagedListAdapter<Post, RecyclerView.ViewHolder> {

    private final static int VIEW_TYPE_POST = 0;
    private final static int VIEW_TYPE_STATE = 1;

    private PostAdapterListener listener;
    private GlideRequests glideRequests;
    private NetworkState networkState = null;

    public PostsAdapter(PostAdapterListener listener, GlideRequests glideRequests) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        this.glideRequests = glideRequests;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case VIEW_TYPE_POST:
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.post_item, viewGroup, false);
                holder = new PostViewHolder(itemView, listener, glideRequests);
                break;
            case VIEW_TYPE_STATE:
                itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.pagging_error_item, viewGroup, false);
                holder = new StateViewHolder(itemView, listener);
                break;
            default:
                throw new UnsupportedOperationException("Unknown view type:");
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case VIEW_TYPE_POST:
                ((PostViewHolder) viewHolder).bind(getItem(position));
                break;
            case VIEW_TYPE_STATE:
                ((StateViewHolder) viewHolder).bind(networkState);
                break;
            default:
                throw new UnsupportedOperationException("Unknown view type:");
        }
    }

    @Override
    public int getItemCount() {
        return hasExtraRow() ? super.getItemCount() + 1 : super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return hasExtraRow() && position == getItemCount() - 1 ? VIEW_TYPE_STATE :
                VIEW_TYPE_POST;
    }

    private boolean hasExtraRow() {
        return networkState != null && networkState.getStatus() != NetworkState.loading().getStatus();
    }

    public void setNetworkState(NetworkState newNetworkState) {
        if (newNetworkState == null) return;
        NetworkState previousState = this.networkState;
        boolean hadExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean hasExtraRow = hasExtraRow();
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount());
            } else {
                notifyItemInserted(super.getItemCount());
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    private static DiffUtil.ItemCallback<Post> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Post>() {
                // The ID property identifies when items are the same.
                @Override
                public boolean areItemsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
                    return oldItem.getName().equals(newItem.getName());
                }

                // Use Object.equals() to know when an item's content changes.
                // Implement equals(), or write custom data comparison logic here.
                @Override
                public boolean areContentsTheSame(@NonNull Post oldItem,  @NonNull Post newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public interface PostAdapterListener {
        void onPostClicked(int position);
        void onRetryClicked();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        GlideRequests glideRequests;
        ImageView thumbnail;
        TextView title;
        TextView subtitle;
        TextView score;
        PostAdapterListener listener;

        PostViewHolder(View itemView, PostAdapterListener listener, GlideRequests glideRequests) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            score = itemView.findViewById(R.id.score);
            this.glideRequests = glideRequests;
            this.listener = listener;
            itemView.setOnClickListener(view -> listener.onPostClicked(getAdapterPosition()));
        }

        void bind(Post post) {
            if (post == null) return;
            String t = post.getTitle() != null ? post.getTitle() : "loading";
            String st = itemView.getResources().getString(R.string.post_subtitle,
                    post.getAuthor() != null ? post.getAuthor() : "unknown");
            title.setText(t);
            subtitle.setText(st);
            score.setText(String.valueOf(post.getScore()));

            if (post.getThumbnail().startsWith("http")) {
                thumbnail.setVisibility(View.VISIBLE);
                glideRequests.load(post.getThumbnail())
                        .centerCrop()
                        .placeholder(R.drawable.ic_insert_photo_black_24dp)
                        .into(thumbnail);
            } else thumbnail.setVisibility(View.GONE);
        }

    }

    static class StateViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;
        TextView error;
        Button retry;
        PostAdapterListener listener;

        StateViewHolder(View itemView, PostAdapterListener listener) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar);
            retry = itemView.findViewById(R.id.retry_button);
            error = itemView.findViewById(R.id.error_msg);
            this.listener = listener;
            retry.setOnClickListener(view -> listener.onRetryClicked());
        }

        void bind(NetworkState networkState) {
            progressBar.setVisibility(toVisbility(networkState != null && networkState.getStatus() == RUNNING));
            retry.setVisibility(toVisbility(networkState != null && networkState.getStatus() == FAILED));
            error.setVisibility(toVisbility(networkState != null && networkState.getMsg() != null));
            if (networkState != null && networkState.getMsg() != null) error.setText(networkState.getMsg());
        }

        int toVisbility(boolean constraint) {
            return constraint ? View.VISIBLE : View.GONE;
        }

    }
}
