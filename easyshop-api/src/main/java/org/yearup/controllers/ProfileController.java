package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("profile")
@CrossOrigin
public class ProfileController {

    private ProfileDao profileDao;
    private UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }


    @GetMapping
    public Profile getProfile(Principal principal) {

        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            return profileDao.getByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping
    public Profile updateProfile(Principal principal, @RequestBody Profile newProfile) {

        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

             if(newProfile.getFirstName() != null) {
                 profileDao.updateProfile(userId, "first_name", newProfile.getFirstName());
             }
             if (newProfile.getLastName() != null) {
                 profileDao.updateProfile(userId, "last_name", newProfile.getLastName());
             }
             if (newProfile.getPhone() != null) {
                 profileDao.updateProfile(userId, "phone", newProfile.getPhone());
             }
             if (newProfile.getEmail() != null) {
                 profileDao.updateProfile(userId, "email", newProfile.getEmail());
             }
             if(newProfile.getAddress() != null) {
                 profileDao.updateProfile(userId, "address", newProfile.getAddress());
             }
             if(newProfile.getCity() != null) {
                 profileDao.updateProfile(userId, "city", newProfile.getCity());
             }
             if(newProfile.getState() != null) {
                 profileDao.updateProfile(userId, "state", newProfile.getState());
             }
             if(newProfile.getZip() != null) {
                 profileDao.updateProfile(userId, "zip", newProfile.getZip());
             }

            return profileDao.getByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
