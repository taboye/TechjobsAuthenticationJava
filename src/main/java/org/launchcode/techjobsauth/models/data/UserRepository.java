package org.launchcode.techjobsauth.models.data;

import org.launchcode.techjobsauth.models.Job;
import org.launchcode.techjobsauth.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    //custom query method
    User findByUsername(String username);
}
