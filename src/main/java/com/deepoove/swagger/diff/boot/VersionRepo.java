
package com.deepoove.swagger.diff.boot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionRepo extends JpaRepository<Version, Long> {

	Version findByName(String name);

}
