package dod.app.beaconscanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BeaconListAdapter extends RecyclerView.Adapter<BeaconListAdapter.ItemViewHolder>{

    Collection<Beacon> beacons;

    public BeaconListAdapter(Collection<Beacon> beacons) {
        this.beacons = beacons;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_list, parent, false);
        return new BeaconListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.distance.setText(beacons.iterator().next().getDistance() + "m");
        holder.uuid.setText(beacons.iterator().next().getId1().toString());
        holder.major.setText(beacons.iterator().next().getId2().toString());
        holder.minor.setText(beacons.iterator().next().getId3().toString());
    }

    @Override
    public int getItemCount() {
        return beacons.size();
    }

    public class ItemViewHolder  extends RecyclerView.ViewHolder{
        TextView distance;
        TextView uuid;
        TextView major;
        TextView minor;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            distance = itemView.findViewById(R.id.distance);
            uuid = itemView.findViewById(R.id.uuid);
            major = itemView.findViewById(R.id.major);
            minor = itemView.findViewById(R.id.minor);
        }
    }
}
