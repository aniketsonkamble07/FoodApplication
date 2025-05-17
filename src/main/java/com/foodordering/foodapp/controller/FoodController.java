package com.foodordering.foodapp.controller;

import com.foodordering.foodapp.exception.DuplicateFoodItemException;
import com.foodordering.foodapp.exception.FoodItemNotFoundException;
import com.foodordering.foodapp.model.FoodItem;
import com.foodordering.foodapp.service.FoodItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

import static org.springframework.beans.support.PagedListHolder.DEFAULT_PAGE_SIZE;

@Controller
@RequestMapping("/admin")
public class FoodController {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final long MAX_IMAGE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final String DEFAULT_SORT_FIELD = "name";
    @Autowired
   FoodItemService foodItemService;

    @GetMapping("/add")
    public String showAddItemForm(Model model) {
        if (!model.containsAttribute("foodItem")) {
            model.addAttribute("foodItem", new FoodItem());
        }
        model.addAttribute("categories", foodItemService.getAllCategories());
        return "addItemPage";
    }

    @PostMapping("/add")
    public String addItemSubmit(@Valid @ModelAttribute("foodItem") FoodItem foodItem,
                                BindingResult result,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.foodItem",
                    result
            );
            redirectAttributes.addFlashAttribute("foodItem", foodItem);
            return "redirect:/admin/add";
        }

        try {
            validateImageFile(imageFile);
            if (!imageFile.isEmpty()) {
                foodItem.setImageData(imageFile.getBytes());
            }

            FoodItem savedItem = foodItemService.createItem(foodItem);
            redirectAttributes.addFlashAttribute("success",
                    "Item '" + savedItem.getName() + "' added successfully!");
            return "redirect:/admin/items";

        } catch (DuplicateFoodItemException e) {
            result.rejectValue("name", "duplicate", e.getMessage());
            model.addAttribute("categories", foodItemService.getAllCategories());
            return "addItemPage";
        } catch (IOException e) {
            result.rejectValue("imageData", "upload.failed", "Failed to process image");
            model.addAttribute("categories", foodItemService.getAllCategories());
            return "addItemPage";
        }
    }

    private void validateImageFile(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Please upload an image file.");
        }

        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid file type. Please upload an image.");
        }

        if (imageFile.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("Image size should not exceed 2MB.");
        }
    }


    @GetMapping("/items")
    public String listItems(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(required = false) String category,
                            Model model) {
        Page<FoodItem> items = category == null
                ? foodItemService.getAllItems(PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by(DEFAULT_SORT_FIELD)))
                : foodItemService.getItemsByCategory(category, PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by(DEFAULT_SORT_FIELD)));

        model.addAttribute("foodItems", items);
        model.addAttribute("categories", foodItemService.getAllCategories());
        return "itemList";
    }


    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            foodItemService.deleteItem(id);
            redirectAttributes.addFlashAttribute("success", "Item deleted successfully");
        } catch (FoodItemNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/items";
    }

    @GetMapping("/items/{id}/edit")
    public String showEditItemForm(@PathVariable Long id, Model model) {
        FoodItem item = foodItemService.getItemById(id)
                .orElseThrow(() -> new FoodItemNotFoundException("Item not found with id: " + id));

        if (!model.containsAttribute("foodItem")) {
            model.addAttribute("foodItem", item);
        }
        model.addAttribute("categories", foodItemService.getAllCategories());
        return "updateItemPage";
    }

    @PostMapping("/items/{id}/edit")
    public String updateItem(@PathVariable Long id,
                             @Valid @ModelAttribute("foodItem") FoodItem foodItem,
                             BindingResult result,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                             @RequestParam(value = "removeImage", required = false) Boolean removeImage,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.foodItem", result);
            redirectAttributes.addFlashAttribute("foodItem", foodItem);
            return "redirect:/admin/items/" + id + "/edit";
        }

        try {
            if (Boolean.TRUE.equals(removeImage)) {
                foodItem.setImageData(null);
            } else if (imageFile != null && !imageFile.isEmpty()) {
                validateImageFile(imageFile);
                foodItem.setImageData(imageFile.getBytes());
            } else {
                // Retain the existing image if none uploaded
                byte[] existingImage = foodItemService.getItemById(id)
                        .map(FoodItem::getImageData)
                        .orElse(null);
                foodItem.setImageData(existingImage);
            }

            FoodItem updatedItem = foodItemService.updateItem(foodItem);
            redirectAttributes.addFlashAttribute("success", "Item '" + updatedItem.getName() + "' updated successfully!");
            return "redirect:/admin/items";

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to process image: " + e.getMessage());
            redirectAttributes.addFlashAttribute("foodItem", foodItem);
            return "redirect:/admin/items/" + id + "/edit";
        }
    }



}
