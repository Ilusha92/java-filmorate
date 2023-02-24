package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping ("directors")
@Slf4j
public class DirectorController {
    private final DirectorService directorService;

   @GetMapping
   public ResponseEntity<List<Director>> getAll () {
       log.info("Получен запрос на список директоров");
       List<Director> directorList = directorService.getAll();
       return new ResponseEntity<>(directorList, HttpStatus.OK);
   }

   @GetMapping ("{id}")
   public ResponseEntity<Director> getById (@PathVariable Integer id) {
       log.info("Получен запрос на директора по id " + id);
       Director director = directorService.getById(id);
       return new ResponseEntity<>(director, HttpStatus.OK);
   }



   @PostMapping
   public ResponseEntity<Director> create (@RequestBody Director director) {
       log.info("Получен запрос на создание директора");
       Director createdDirector = directorService.create(director);
       return new ResponseEntity<>(createdDirector, HttpStatus.OK);
   }


    @PutMapping
    public ResponseEntity<Director> update (@RequestBody Director director) {
        log.info("Получен запрос на обновление директора с id " + director.getId());
        Director updatedDirector = directorService.update(director);
        return new ResponseEntity<>(updatedDirector, HttpStatus.OK);
    }

    @DeleteMapping ("{id}")
    public ResponseEntity<Director> delete (@PathVariable Integer id) {
        log.info("Получен запрос на удаление директора с id " + id);
        Director director = directorService.delete(id);
        return new ResponseEntity<>(director, HttpStatus.OK);
    }
}
