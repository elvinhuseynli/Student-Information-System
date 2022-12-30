package com.example.sis.admin.requests;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sis.R;
import com.example.sis.admin.courses.CourseDetailsConstants;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.w3c.dom.Text;

import java.util.List;

public class AdminRequestsAdapter extends RecyclerView.Adapter<AdminRequestsAdapter.RequestsHolder> {

    List<AuthenticationConstants> authenticationConstantsList;
    AdminRequestsListener adminRequestsListener;

    public AdminRequestsAdapter(List<AuthenticationConstants> authenticationConstantsList, AdminRequestsListener adminRequestsListener) {
        this.adminRequestsListener = adminRequestsListener;
        this.authenticationConstantsList = authenticationConstantsList;
    }

    @Override
    public RequestsHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.admin_requests_adapter, parent, false);
        return new RequestsHolder(view);
    }

    @Override
    public void onBindViewHolder(RequestsHolder holder, int position) {
        holder.setData(authenticationConstantsList.get(position));
    }

    public void setVisibility(List<AuthenticationConstants> filteredList, boolean isVisible) {
        if(!isVisible)
            authenticationConstantsList.clear();
        authenticationConstantsList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return authenticationConstantsList.size();
    }


    class RequestsHolder extends RecyclerView.ViewHolder {

        TextView fullName, studentId, registrationDate, department;
        CardView cardView;

        public RequestsHolder(@NonNull View view) {
            super(view);
            fullName = (TextView) view.findViewById(R.id.fullName);
            studentId = (TextView) view.findViewById(R.id.studentId);
            registrationDate = (TextView) view.findViewById(R.id.registrationDate);
            department = (TextView) view.findViewById(R.id.department);
            cardView = (CardView) view.findViewById(R.id.admin_requests_adapter);
        }

        public void setData(AuthenticationConstants authenticationConstants) {
            fullName.setText(authenticationConstants.getFullName());
            studentId.setText(authenticationConstants.getStudentId());
            registrationDate.setText(authenticationConstants.getRegistrationDate());
            department.setText(authenticationConstants.getDepartment());

            cardView.setOnClickListener(v -> adminRequestsListener.onRequestClicked(authenticationConstants));
        }

    }
}
