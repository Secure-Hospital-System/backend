package com.bezkoder.spring.security.postgresql.controllers;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.bezkoder.spring.security.postgresql.payload.response.PatientDiagnosisResponse;
import com.bezkoder.spring.security.postgresql.payload.response.PatientProfileResponse;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/patient")
public class PatientController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

	@GetMapping("/profile/{id}")
    @PreAuthorize("hasRole('PATIENT')")
	public ResponseEntity<?> getPatientProfile(@PathVariable long id) {
        Connection c = null;
        Statement stmt = null;
        int age = -1;
        String name = "", gender = "", address = "", phoneNumber = "", creditCard = "";
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://ec2-44-202-162-44.compute-1.amazonaws.com:5432/postgres","backend", "CSE545_SS_backend");
            System.out.println("Successfully Connected.");  
            stmt = (Statement) c.createStatement();

            String sql = "SELECT name, age, gender, address, \"phoneNumber\", \"creditCard\" FROM public.user as u, public.patient as p where u.\"userID\" = p.\"patientID\" and u.\"userID\"="+id+"";

            ResultSet rs = stmt.executeQuery(sql);
            while ( rs.next() ) {
                age = rs.getInt("age");
                name = rs.getString("name");
                gender  = rs.getString("gender");
                address  = rs.getString("address");
                phoneNumber  = rs.getString("phoneNumber");
                creditCard  = rs.getString("creditCard");
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
          }
        return ResponseEntity.ok(new PatientProfileResponse(name, age, gender, address, phoneNumber, creditCard));
	}

    @GetMapping("/diagnosis/{id}")
    @PreAuthorize("hasRole('PATIENT')")
	public ResponseEntity<?> getPatientDiagnosis(@PathVariable long id) {
        Connection c = null;
        Statement stmt = null;
        int doctorID = -1;
        List<String> diagnosis = new ArrayList<String>();
        Date date = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://ec2-44-202-162-44.compute-1.amazonaws.com:5432/postgres","backend", "CSE545_SS_backend");
            System.out.println("Successfully Connected.");  
            stmt = (Statement) c.createStatement();

            String sql = "SELECT * FROM public.diagnosis as d, public.patient as p where d.\"patientID\" = p.\"patientID\" and p.\"patientID\"="+id;

            ResultSet rs = stmt.executeQuery(sql);
            while ( rs.next() ) {
                doctorID = rs.getInt("doctorID");
                date  = rs.getDate("date");
                diagnosis.add(rs.getString("diagnosis"));
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
        System.err.println( e.getClass().getName()+": "+ e.getMessage() );
          }
        return ResponseEntity.ok(new PatientDiagnosisResponse(doctorID, date, diagnosis));
	}
	
    @GetMapping("/details/id")
	@PreAuthorize("hasRole('PATIENT')")
	public String userAccess1() {
		return "patient Content.";
	}

	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String userAccess() {
		return "User Content.";
	}

	@GetMapping("/mod")
	@PreAuthorize("hasRole('MODERATOR')")
	public String moderatorAccess() {
		return "Moderator Board.";
	}
 
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Admin Board.";
	}
}