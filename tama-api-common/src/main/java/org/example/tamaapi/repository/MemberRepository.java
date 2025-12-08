package org.example.tamaapi.repository;

import org.example.tamaapi.domain.user.Authority;
import org.example.tamaapi.domain.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
