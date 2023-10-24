package com.app.todo.Repository;

import com.app.todo.modules.Todo;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

@Transactional
public interface TodoRepository extends JpaRepository<Todo, Long> {
    /*
        @Query(value = "SELECT t FROM Todo t WHERE t.status = 'false' and  t.customer.id = :id ORDER BY t.id DESC")
        Page<Todo> getTodoList(@Param("id") UUID customerId, Pageable pageable);

        @Query(value = "SELECT t FROM Todo t WHERE t.status ='true' and  t.customer.id = :id ORDER BY t.id DESC")
        Page<Todo> getDoneList(@Param("id") UUID customerId, Pageable pageable);

        @Query(value = "SELECT t from Todo t where t.id=:id and t.customer.id =:customerId")
        Optional<Todo> findTodoByIdAndCustomerId(@Param("id") Long id, @Param("customerId") UUID customerId);
    */
    Page<Todo> findTodosByCustomerId(UUID customerId, Pageable pageable);

    Optional<Todo> findTodoByIdAndCustomerId(@Param("id") Long id, @Param("customerId") UUID customerId);

}
