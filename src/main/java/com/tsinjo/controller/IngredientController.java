package com.tsinjo.controller;

import com.tsinjo.model.IngredientCategory;
import com.tsinjo.model.IngredientsItem;
import com.tsinjo.request.IngredientCategoryRequest;
import com.tsinjo.request.IngredientRequest;
import com.tsinjo.service.IngredientsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/ingredients")
public class IngredientController {

    @Autowired
    private IngredientsService ingredientsService;


    @PostMapping("category")
    public ResponseEntity<IngredientCategory> createIngredientCategory(
            @RequestBody IngredientCategoryRequest req) throws Exception {
        IngredientCategory item=ingredientsService.createIngredientsCategory(req.getName(), req.getRestaurantId());
        return  new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    @PostMapping()
    public ResponseEntity<IngredientsItem> createIngredientItem(
            @RequestBody IngredientRequest req) throws Exception {
        IngredientsItem item=ingredientsService.createIngredientItem(req.getRestaurantId(), req.getName(),req.getCategoryId());
        return  new ResponseEntity<>(item, HttpStatus.CREATED);
    }

   @PutMapping("/{id}/stoke")
    public ResponseEntity<IngredientsItem> updateIngredientStock(
            @RequestBody IngredientRequest req,
            @PathVariable Long id
   ) throws Exception {
        IngredientsItem item=ingredientsService.updateStock(id);
        return  new ResponseEntity<>(item, HttpStatus.OK);
    }

    @GetMapping("/restaurant/{id}")
    public ResponseEntity<List<IngredientsItem>> getRestaurantIngredient(
            @RequestBody IngredientRequest req,
            @PathVariable Long id
    ) throws Exception {
        List<IngredientsItem> item=ingredientsService.findRestaurantIngredients(id);
        return  new ResponseEntity<>(item, HttpStatus.OK);
    }


    @GetMapping("/restaurant/{id}/category")
    public ResponseEntity<List<IngredientCategory>> getRestaurantIngredientCategory(
            @RequestBody IngredientRequest req,
            @PathVariable Long id
    ) throws Exception {
        List<IngredientCategory> item=ingredientsService.findIngredientCategoryByRestaurantId(id);
        return  new ResponseEntity<>(item, HttpStatus.OK);
    }
}
