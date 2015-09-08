package com.jaspersoft.android.jaspermobile.activities.inputcontrols;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.jaspersoft.android.jaspermobile.R;
import com.jaspersoft.android.sdk.client.oxm.control.InputControlOption;

import java.util.List;

/**
 * @author Andrew Tivodar
 * @since 2.2
 */
public class MultiSelectIcAdapter extends RecyclerView.Adapter<MultiSelectIcAdapter.SingleSelectViewHolder> {

    private List<InputControlOption> mInputControlOptions;

    public MultiSelectIcAdapter(List<InputControlOption> inputControlOptions) {
        if (inputControlOptions == null) {
            throw new IllegalArgumentException("Input Controls Options list can not be null!");
        }
        this.mInputControlOptions = inputControlOptions;
    }

    @Override
    public SingleSelectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_input_control_boolean, parent, false);
        return new SingleSelectViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(SingleSelectViewHolder viewHolder, int position) {
        viewHolder.populateView(mInputControlOptions.get(position));
    }

    @Override
    public int getItemCount() {
        return mInputControlOptions.size();
    }

    private void onItemSelected(int position){
        InputControlOption inputControlOption = mInputControlOptions.get(position);
        inputControlOption.setSelected(!inputControlOption.isSelected());
        notifyItemChanged(position);
    }

    protected class SingleSelectViewHolder extends RecyclerView.ViewHolder {

        private CheckBox cbSingleSelect;

        public SingleSelectViewHolder(View itemView) {
            super(itemView);
            cbSingleSelect = (CheckBox) itemView;
            cbSingleSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSelected(getPosition());
                }
            });
        }

        public void populateView(InputControlOption inputControlOption) {
            cbSingleSelect.setText(inputControlOption.getLabel());
            cbSingleSelect.setChecked(inputControlOption.isSelected());
        }
    }
}