package com.example.springJDBC.controller;
import com.example.springJDBC.DTO.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/create")
    public ResponseEntity<String> create() {

        logger.info("Customer Table is: CREATE");
        jdbcTemplate.execute("DROP TABLE IF EXISTS customers");
        jdbcTemplate.execute("CREATE TABLE customers (" +
                "id SERIAL, first_name VARCHAR(50), last_name VARCHAR(50)" +
                ")");

        return new ResponseEntity<>("Customer table is Created", HttpStatus.CREATED);
    }

    @PostMapping("/insert")
    public ResponseEntity<User> insert(@RequestBody User user) {
        logger.info("Inserted Data:\t" + user.getFirst_name() + "\t" + user.getLast_name());

        jdbcTemplate.update("INSERT INTO customers(first_name, last_name) VALUES(?,?)", user.getFirst_name(), user.getLast_name());
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUser() {
        List<User> users = null;
        try {
            String sql = "SELECT id, first_name, last_name FROM customers";

            RowMapper<User> userMapper = (rs, row) ->
                    User.builder()
                            .id(rs.getInt("id"))
                            .first_name(rs.getString("first_name"))
                            .last_name(rs.getString("last_name")).build();

            users = jdbcTemplate.query(sql, userMapper);

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable("id") String id) {
        Optional<User> user = null;
        List<User> users = null;
        try {
            String sql = "SELECT id, first_name, last_name FROM customers WHERE id=" + id;

//            RowMapper<User> userMapper = (rs, row) ->
//                    User.builder()
//                            .id(rs.getInt("id"))
//                            .first_name(rs.getString("first_name"))
//                            .last_name(rs.getString("last_name")).build();


            RowMapper<User> userMapper = (rs, row) ->
                    new User(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getInt("id")
                    );


            users = jdbcTemplate.query(sql, userMapper);
            user = users.stream().findFirst();

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<User> updateById(@PathVariable("id") int id, @RequestBody User user) {

        String sql = "UPDATE customers SET last_name=? WHERE id= ?";
        jdbcTemplate.update(sql, user.getLast_name(), id);
        logger.info("Update Query Executed 🤞");

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> deleteById(@PathVariable("id") int id) {

        String sql = "DELETE FROM customers WHERE id= ?";
        int update = jdbcTemplate.update(sql, id);

        logger.info("Delete Query Executed 🤞");

        return new ResponseEntity<>(update, HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {

        logger.info("WORKING - TEST 👍");
        return new ResponseEntity<>("URL is working", HttpStatus.CREATED);
    }

}
