package com.example.realestateapp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.example.realestateapp.screens.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthHelper {
    
    /**
     * Check if user is logged in
     */
    public static boolean isUserLoggedIn() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }
    
    /**
     * Show login dialog and redirect to login if user is not logged in
     * @param context The context to show dialog and start activity
     * @param message Message to show in dialog
     * @return true if user is logged in, false otherwise
     */
    public static boolean requireLogin(Context context, String message) {
        if (!isUserLoggedIn()) {
            new AlertDialog.Builder(context)
                    .setTitle("Login Required")
                    .setMessage(message)
                    .setPositiveButton("Login", (dialog, which) -> {
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
            return false;
        }
        return true;
    }
    
    /**
     * Check login and show toast if not logged in
     */
    public static boolean checkLoginWithToast(Context context, String action) {
        if (!isUserLoggedIn()) {
            String message = "Please login to " + action;
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            requireLogin(context, "You need to login to " + action);
            return false;
        }
        return true;
    }
}
