package com.example.Docgen_Backend.service.impl;

import com.example.Docgen_Backend.dto.UserProfileRequest;
import com.example.Docgen_Backend.entity.User;
import com.example.Docgen_Backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Save User
    public User saveUser(UserProfileRequest request) {

        User user = new User();

        setUserData(user, request);

        return userRepository.save(user);
    }

    // Get All Users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get User By ID
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Update User
    public User updateUser(Long id, UserProfileRequest request) {

        User existingUser = userRepository.findById(id).orElse(null);

        if (existingUser != null) {

            setUserData(existingUser, request);

            return userRepository.save(existingUser);
        }

        return null;
    }

    // Delete User
    public String deleteUser(Long id) {

        if (userRepository.existsById(id)) {

            userRepository.deleteById(id);

            return "User deleted successfully";
        }

        return "User not found";
    }

    // Common Method
    private void setUserData(User user, UserProfileRequest request) {

        user.setCompany(request.getCompany());
        user.setIdentity(request.getIdentity());
        user.setFullName(request.getFullName());
        user.setEmployeeId(request.getEmployeeId());

        user.setMobileNo(request.getMobileNo());
        user.setEmail(request.getEmail());
        user.setPanNo(request.getPanNo());
        user.setDateOfBirth(request.getDateOfBirth());

        user.setCurrentAddress(request.getCurrentAddress());
        user.setPermanentAddress(request.getPermanentAddress());

        user.setOfferDate(request.getOfferDate());
        user.setJoiningDate(request.getJoiningDate());
        user.setJoiningCtc(request.getJoiningCtc());
        user.setCurrentCtc(request.getCurrentCtc());

        user.setJoiningDesignation(request.getJoiningDesignation());
        user.setCurrentDesignation(request.getCurrentDesignation());
        user.setDepartment(request.getDepartment());

        user.setBankName(request.getBankName());
        user.setAccountNo(request.getAccountNo());
        user.setPfType(request.getPfType());
    }
}