package com.hirantha.admins;

import com.hirantha.quries.admins.AdminQueries;
import com.hirantha.models.data.admins.Admin;

public class CurrentAdmin {

    private Admin currentAdmin;

    private static CurrentAdmin instance;

    private CurrentAdmin() {
    }

    public static CurrentAdmin getInstance() {
        if (instance == null) instance = new CurrentAdmin();
        return instance;
    }

    public Admin getCurrentAdmin() {
        return currentAdmin;
    }

    private void setCurrentAdmin(Admin currentAdmin) {
        this.currentAdmin = currentAdmin;
    }

    public boolean logginAdmin(String username, String password) {

        setCurrentAdmin(AdminQueries.getInstance().getAdmin(username, password));
        return getCurrentAdmin() != null;
    }
}
