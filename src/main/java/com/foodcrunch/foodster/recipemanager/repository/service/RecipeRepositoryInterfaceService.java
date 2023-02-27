package com.foodcrunch.foodster.recipemanager.repository.service;

import com.foodcrunch.foodster.recipemanager.model.entity.*;
import com.foodcrunch.foodster.recipemanager.repository.*;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeRepositoryInterfaceService implements RecipeRepositoryInterface {

    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;
    private final FoodRepository foodRepository;
    private final StepRepository stepRepository;
    private final ImageRepository imageRepository;

    @PersistenceContext
    private EntityManager em;

    public Page<RecipeEntity> findByPagingCriteria(Pageable pageable, Map<String, String> param) {
        Page page = recipeRepository.findAll(new Specification<RecipeEntity>() {
            @Override
            public Predicate toPredicate(Root<RecipeEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isEmpty(param.get("isVisible"))) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("visible"), param.get("isVisible"))));
                }
                if (!StringUtils.isEmpty(param.get("name"))) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("name"), "%" + param.get("name") + "%")));
                }
                if (!StringUtils.isEmpty(param.get("about"))) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("about"), "%" + param.get("about") + "%")));
                }
                if (!StringUtils.isEmpty(param.get("type"))) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("type"), param.get("type"))));
                }
                if (!StringUtils.isEmpty(param.get("userId")) && StringUtils.isEmpty(param.get("isVisible"))) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("userId"), param.get("userId"))));
                }
                if (!StringUtils.isEmpty(param.get("level"))) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("level"), param.get("level"))));
                }
                if (!StringUtils.isEmpty(param.get("lang"))) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("lang"), param.get("lang"))));
                }
                if (!StringUtils.isEmpty(param.get("ingredient"))) {
                    String[] separatedString = param.get("ingredient").split(",");

                    Subquery<Long> subFoodstuff = criteriaQuery.subquery(Long.class);
                    Root<ProductEntity> subFoodstuffProject = subFoodstuff.from(ProductEntity.class);
                    subFoodstuff.select(subFoodstuffProject.get("id"));

                    subFoodstuff.where(criteriaBuilder.and(subFoodstuffProject.get("name").in(separatedString)));

                    Subquery<Long> subCookFood = criteriaQuery.subquery(Long.class);
                    Root<FoodEntity> subCookFoodProject = subCookFood.from(FoodEntity.class);
                    subCookFood.select(subCookFoodProject.get("recipe"));
                    subCookFood.where(criteriaBuilder.and(subCookFoodProject.get("foodstuffEntity").in(subFoodstuff)));

                    predicates.add(criteriaBuilder.and(root.get("id").in(subCookFood)));
                }
                if (/*param.get("minServe")!=null && */param.get("maxServe")!=null) {
//                        predicates.add(criteriaBuilder.between(
//                                root.get("serve"), Integer.valueOf(param.get("minServe")), Integer.valueOf(param.get("maxServe"))
//                        ));
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("serve"), Integer.valueOf(param.get("maxServe"))));
                }
                if(!StringUtils.isEmpty(param.get("minTime")) && !StringUtils.isEmpty(param.get("maxTime"))) {
                    predicates.add(criteriaBuilder.between(root.get("time"), Integer.valueOf(param.get("minTime")), Integer.valueOf(param.get("maxTime"))));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
        return page;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public void saveRecipe(RecipeEntity recipeEntity) {

        Query getRecipeSeq = em.createNativeQuery("SELECT nextval('recipe.recipe_seq')");

        Query insertRecipe = em.createNativeQuery("INSERT INTO RECIPE.RECIPE (about, date, language, level, name, serve, time, type, user_id, visible, id) VALUES (:about, :date, :language, :level, :name, :serve, :time, :type, :user_id, :visible, :id)");

//Save main information about recipe
        BigInteger toLong = (BigInteger) getRecipeSeq.getSingleResult();

        recipeEntity.setId(toLong.longValue());
        insertRecipe.setParameter("about", recipeEntity.getAbout())
                .setParameter("date", recipeEntity.getDate())
                .setParameter("language", recipeEntity.getLang())
                .setParameter("level", recipeEntity.getLevel())
                .setParameter("name", recipeEntity.getName())
                .setParameter("serve", recipeEntity.getServe())
                .setParameter("time", recipeEntity.getTime())
                .setParameter("type", recipeEntity.getType())
                .setParameter("user_id", recipeEntity.getUserId())
                .setParameter("visible", recipeEntity.getVisible())
                .setParameter("id", recipeEntity.getId())
                .executeUpdate();

        recipeEntity.getFoodEntity().forEach( (f) -> {
            insertFood(f, recipeEntity.getId());
        });

        recipeEntity.getStepEntity().forEach(n -> {
            insertStep(n, recipeEntity.getId());
        });

        if (recipeEntity.getImageEntity().size() != 0) {
            recipeEntity.getImageEntity().forEach(n -> {
                insertImage(n, recipeEntity.getId());
            });
        }

    }

    @Override
    public void updateRecipe(RecipeEntity recipeEntity) {
        RecipeEntity existingRecipe = recipeRepository.findAllById(recipeEntity.getId());

        Query updateRecipe = em.createNativeQuery("UPDATE RECIPE.RECIPE SET about = :about, language = :language,level = :level, name = :name, serve = :serve, time = :time, type = :type, visible = :visible WHERE id = :id");

        Query updateImage = em.createNativeQuery("UPDATE RECIPE.IMAGE i set i.IMAGE_REFERENCE = :reference WHERE i.id = :id");
        Query insertImage = em.createNativeQuery("INSERT INTO RECIPE.IMAGE (IMAGE_REFERENCE, recipe_id, id) VALUES (:image, :recipe_id, nextval('recipe.image_seq'))");

        updateRecipe.setParameter("about", recipeEntity.getAbout())
                .setParameter("language", recipeEntity.getLang())
                .setParameter("level", recipeEntity.getLevel())
                .setParameter("name", recipeEntity.getName())
                .setParameter("serve", recipeEntity.getServe())
                .setParameter("time", recipeEntity.getTime())
                .setParameter("type", recipeEntity.getType())
                .setParameter("visible", recipeEntity.getVisible())
                .setParameter("id", recipeEntity.getId())
                .executeUpdate();

        //update ingredients
        List<FoodEntity> newIngredients = new ArrayList<>(recipeEntity.getFoodEntity());
        List<FoodEntity> oldIngredients = new ArrayList<>(existingRecipe.getFoodEntity());
        newIngredients = newIngredients.stream().sorted(Comparator.comparing(FoodEntity::getId)).collect(Collectors.toList());
        oldIngredients = oldIngredients.stream().sorted(Comparator.comparing(FoodEntity::getId)).collect(Collectors.toList());

        if (newIngredients.size() == oldIngredients.size()) {

            for (int i = 0; i < newIngredients.size(); i++) {
                FoodEntity newTmpFood = newIngredients.get(i);
                FoodEntity oldTmpFood = oldIngredients.get(i);
                checkIngredients(newIngredients, oldTmpFood, newTmpFood, recipeEntity.getId());
            }
            removeFood(oldIngredients, newIngredients);

        } else if (newIngredients.size() < oldIngredients.size()) {
            Integer difference = 0;
            for (int i = 0; i < oldIngredients.size(); i++) {
                FoodEntity newTmpFood = newIngredients.get(i - difference);
                FoodEntity oldTmpFood = oldIngredients.get(i);

                if (newTmpFood.getId() == 0) {
                    insertFood(newTmpFood, recipeEntity.getId());
                } else {
                    if (oldTmpFood.getId() == newTmpFood.getId()) {
                        if (!oldTmpFood.getUnit().equals(newTmpFood.getUnit()) || !oldTmpFood.getAmount().equals(newTmpFood.getAmount())) {

                            if (!newTmpFood.getProductEntity().getName().equals(oldTmpFood.getProductEntity().getName())) {
                                ProductEntity newProduct = insertProduct(newTmpFood.getProductEntity());
                                newTmpFood.setProductEntity(newProduct);
                                updateFood(newTmpFood);
                            } else {
                                updateFood(newTmpFood);
                            }

                        } else {
                            if (!newTmpFood.getProductEntity().getName().equals(oldTmpFood.getProductEntity().getName())) {
                                ProductEntity newProduct = insertProduct(newTmpFood.getProductEntity());
                                newTmpFood.setProductEntity(newProduct);
                                updateFood(newTmpFood);
                            } else {
                                difference += 1;
                            }
                        }
                    } else {
                        difference += 1;
                    }
                }

                if (oldTmpFood.getId() == newTmpFood.getId()) {

                    if (!oldTmpFood.getUnit().equals(newTmpFood.getUnit()) || !oldTmpFood.getAmount().equals(newTmpFood.getAmount())) {

                        if (!newTmpFood.getProductEntity().getName().equals(oldTmpFood.getProductEntity().getName())) {
                            ProductEntity newProduct = insertProduct(newTmpFood.getProductEntity());
                            newTmpFood.setProductEntity(newProduct);
                            updateFood(newTmpFood);
                        } else {
                            updateFood(newTmpFood);
                        }

                    }

                } else {

                }
            }
            removeFood(oldIngredients, newIngredients);
        } else if (newIngredients.size() > oldIngredients.size()) {
            Integer difference = 0;
            for (int i = 0; i < newIngredients.size(); i++) {
                FoodEntity newTmpFood = newIngredients.get(i);
                if (oldIngredients.size() == 0) {
                    insertFood(newTmpFood, recipeEntity.getId());
                } else {
                    FoodEntity oldTmpFood = oldIngredients.get(i - difference);
                    difference += checkIngredients(newIngredients, oldTmpFood, newTmpFood, recipeEntity.getId());
                }
            }
            removeFood(oldIngredients, newIngredients);

        }

        //update steps
        List<StepEntity> newSteps = new ArrayList<>(recipeEntity.getStepEntity());
        List<StepEntity> oldSteps = new ArrayList<>(existingRecipe.getStepEntity());
        newSteps = newSteps.stream().sorted(Comparator.comparing(StepEntity::getId)).collect(Collectors.toList());
        oldSteps = oldSteps.stream().sorted(Comparator.comparing(StepEntity::getId)).collect(Collectors.toList());

        if (recipeEntity.getStepEntity().size() == existingRecipe.getStepEntity().size()) {

            for (int i = 0; i < recipeEntity.getStepEntity().size(); i++) {
                StepEntity newTmpStep = newSteps.get(i);
                StepEntity oldTmpStep = oldSteps.get(i);

                if (newTmpStep.getStepNumber() == oldTmpStep.getStepNumber()) {
                    if (newTmpStep.getStepNumber() != oldTmpStep.getStepNumber() || !newTmpStep.getStep().equals(oldTmpStep.getStep()) || !newTmpStep.getImage().equals(oldTmpStep.getImage())) {
                        stepRepository.updateStep(newTmpStep.getImage(), newTmpStep.getStep(), newTmpStep.getStepNumber(), newTmpStep.getId());
                    }
                } else {
                    if (newTmpStep.getId() == 0) {
                        StepEntity existInOld = oldSteps.stream().filter(step -> newTmpStep.getStepNumber() == step.getStepNumber()).findAny().orElse(null);
                        if (existInOld != null) {
                            stepRepository.updateStep(newTmpStep.getImage(), newTmpStep.getStep(), newTmpStep.getStepNumber(), existInOld.getId());
                        }
                    } else {
                        StepEntity existInOld = oldSteps.stream().filter(step -> newTmpStep.getStepNumber() == step.getStepNumber()).findAny().orElse(null);
                        if (existInOld != null) {
                            stepRepository.updateStep(newTmpStep.getImage(), newTmpStep.getStep(), newTmpStep.getStepNumber(), existInOld.getId());
                        }
                    }
                }
            }

        } else if (newSteps.size() < oldSteps.size()) {
            for (int i = 0; i < oldSteps.size(); i++) {
                StepEntity newTmpStep;
                StepEntity oldTmpStep = oldSteps.get(i);

                if (i < newSteps.size()) {
                    newTmpStep = newSteps.get(i);
                    if (newTmpStep.getId() == 0) {
                        StepEntity existInNew = oldSteps.stream().filter(step -> newTmpStep.getStepNumber() == step.getStepNumber()).findAny().orElse(null);
                        if (existInNew != null) {
                            stepRepository.updateStep(newTmpStep.getImage(), newTmpStep.getStep(), newTmpStep.getStepNumber(), existInNew.getId());
                        }
                    } else {
                        if (newTmpStep.getStepNumber() == oldTmpStep.getStepNumber()) {
                            if (newTmpStep.getStepNumber() != oldTmpStep.getStepNumber() || !newTmpStep.getStep().trim().equals(oldTmpStep.getStep().trim()) || !newTmpStep.getImage().equals(oldTmpStep.getImage())) {
                                stepRepository.updateStep(newTmpStep.getImage(), newTmpStep.getStep(), newTmpStep.getStepNumber(), newTmpStep.getId());
                            }
                        } else {
                            StepEntity existInNew = oldSteps.stream().filter(step -> newTmpStep.getStepNumber() == step.getStepNumber()).findAny().orElse(null);
                            if (existInNew != null) {
                                stepRepository.updateStep(newTmpStep.getImage(), newTmpStep.getStep(), newTmpStep.getStepNumber(), existInNew.getId());
                            }
                        }
                    }
                }
            }
            removeStep(oldSteps, newSteps);
        } else if (newSteps.size() > oldSteps.size()) {
            Integer diff = 0;
            for (int i = 0; i < newSteps.size(); i++) {

                StepEntity newTmpStep = newSteps.get(i);

                if (oldSteps.size() == 0 ){
                    insertStep(newTmpStep, recipeEntity.getId());
                } else {
                    StepEntity oldTmpStep = oldSteps.get(i - diff);

                    if (newTmpStep.getStepNumber() == oldTmpStep.getStepNumber()) {
                        if (newTmpStep.getStepNumber() != oldTmpStep.getStepNumber() || !newTmpStep.getStep().equals(oldTmpStep.getStep()) || !newTmpStep.getImage().equals(oldTmpStep.getImage())) {
                            stepRepository.updateStep(newTmpStep.getImage(), newTmpStep.getStep(), newTmpStep.getStepNumber(), newTmpStep.getId());
                        }
                    } else {
                        if (newTmpStep.getId() == 0) {
                            StepEntity existInOld = oldSteps.stream().filter(step -> newTmpStep.getStepNumber() == step.getStepNumber()).findAny().orElse(null);
                            if (existInOld != null) {
                                stepRepository.updateStep(newTmpStep.getImage(), newTmpStep.getStep(), newTmpStep.getStepNumber(), existInOld.getId());
                            } else {
                                insertStep(newTmpStep, recipeEntity.getId());
                                diff += 1;
                            }
                        } else {
                            StepEntity existInOld = oldSteps.stream().filter(step -> newTmpStep.getStepNumber() == step.getStepNumber()).findAny().orElse(null);
                            if (existInOld != null) {
                                stepRepository.updateStep(newTmpStep.getImage(), newTmpStep.getStep(), newTmpStep.getStepNumber(), existInOld.getId());
                            }
                        }
                    }
                }

            }
        }

        List<ImageEntity> newImages = new ArrayList<>(recipeEntity.getImageEntity());
        List<ImageEntity> oldImages = new ArrayList<>(existingRecipe.getImageEntity());
        newImages = newImages.stream().sorted(Comparator.comparing(ImageEntity::getId)).collect(Collectors.toList());
        oldImages = oldImages.stream().sorted(Comparator.comparing(ImageEntity::getId)).collect(Collectors.toList());

        if (newImages.size() == oldImages.size()) {
            for (int i = 0; i < newImages.size(); i++) {
                ImageEntity newTmpImage = newImages.get(i);
                ImageEntity oldTmpImage = oldImages.get(i);
                if (!newTmpImage.getImage().equals(oldTmpImage.getImage())) {
                    imageRepository.updateImage(newTmpImage.getImage(), oldTmpImage.getId());
                }
            }
        }

        if (newImages.size() < oldImages.size()) {
            List<Long> remove = new ArrayList<>();
            for (ImageEntity image : oldImages) {
                remove.add(image.getId());
            }
            imageRepository.deleteImagesByLisId(remove);

        }

        if (newImages.size()> oldImages.size()) {
            for (int i = 0; i < newImages.size(); i++) {
                insertImage(newImages.get(i), recipeEntity.getId());
            }
        }
    }

    private Integer checkIngredients(List<FoodEntity> newIngredients, FoodEntity oldTmpFood, FoodEntity newTmpFood, long recipeId) {

        if (newTmpFood.getId() == 0) {
            insertFood(newTmpFood, recipeId);
            return 1;
        } else {
            FoodEntity existInOld = newIngredients.stream().filter( food -> oldTmpFood.getId() == food.getId()).findAny().orElse(null);
            if (existInOld != null) {
                if (!existInOld.getAmount().equals(oldTmpFood.getAmount()) || !existInOld.getUnit().equals(oldTmpFood.getUnit())) {
                    if (!newTmpFood.getProductEntity().getName().equals(oldTmpFood.getProductEntity().getName())) {
                        ProductEntity newProduct = insertProduct(newTmpFood.getProductEntity());
                        newTmpFood.setProductEntity(newProduct);
                        updateFood(newTmpFood);
                    } else {
                        updateFood(newTmpFood);
                    }
                }
            }
            if (!newTmpFood.getProductEntity().getName().equals(oldTmpFood.getProductEntity().getName()) ) {
                ProductEntity newProduct = insertProduct(newTmpFood.getProductEntity());
                newTmpFood.setProductEntity(newProduct);
                updateFood(newTmpFood);
            }
        }
        return 0;
    }

    private ProductEntity insertProduct(ProductEntity product) {

        Query insertFoodstuff = em.createNativeQuery("INSERT INTO RECIPE.PRODUCT (IMAGE_REFERENCE, name, id) VALUES (:pic, :name, nextval('recipe.product_seq'))");
        Query insertFoodstuffNoPic = em.createNativeQuery("INSERT INTO RECIPE.PRODUCT (name, id) VALUES (:name, nextval('recipe.product_seq'))");

        ProductEntity existProduct = productRepository.findByNameEqualsIgnoreCase(product.getName());

        if (existProduct == null) {
            if (product.getImage() != null) {
                String pic = product.getImage();
                insertFoodstuff
                        .setParameter("name", product.getName().trim())
                        .setParameter("pic", pic)
                        .executeUpdate();
            } else {
                insertFoodstuffNoPic
                        .setParameter("name", product.getName().trim())
                        .executeUpdate();
            }
        } else {
            return existProduct;
        }

        return productRepository.findByNameEqualsIgnoreCase(product.getName().trim());
    }

    private void insertFood(FoodEntity f, long recipeId) {

        Query insertFood = em.createNativeQuery("INSERT INTO RECIPE.FOOD (food_id, unit, recipe_id, amount, id) VALUES (:food_id, :unit, :recipe_id, :amount, nextval('recipe.cookfood_seq'))");

        if (f.getProductEntity().getId() == 0) {
            ProductEntity resultFs = productRepository.findByNameEqualsIgnoreCase(f.getProductEntity().getName().trim());
            if (resultFs == null) {
                resultFs = insertProduct(f.getProductEntity());
            }
            f.setProductEntity(resultFs);
        }

        insertFood
                .setParameter("food_id", f.getProductEntity().getId())
                .setParameter("unit", f.getUnit())
                .setParameter("recipe_id", recipeId)
                .setParameter("amount", f.getAmount())
                .executeUpdate();
    }

    private void insertStep(StepEntity n, long recipeId) {
        Query insertStep = em.createNativeQuery("INSERT INTO RECIPE.STEP (IMAGE_REFERENCE, step, step_number, recipe_id, id) VALUES (:image, :step, :step_number, :recipe_id, nextval('recipe.cookstep_seq'))");
        Query insertStepNoPic = em.createNativeQuery("INSERT INTO RECIPE.STEP (step, step_number, recipe_id, id) VALUES (:step, :step_number, :recipe_id, nextval('recipe.cookstep_seq'))");

        if (n.getImage() != null) {
            String pic = n.getImage();
            insertStep
                    .setParameter("image", pic)
                    .setParameter("step", n.getStep())
                    .setParameter("step_number", n.getStepNumber())
                    .setParameter("recipe_id", recipeId)
                    .executeUpdate();
        } else {
            insertStepNoPic
                    .setParameter("step", n.getStep())
                    .setParameter("step_number", n.getStepNumber())
                    .setParameter("recipe_id", recipeId)
                    .executeUpdate();
        }
    }

    private void insertImage(ImageEntity image, long recipeId) {
        Query insertImage = em.createNativeQuery("INSERT INTO RECIPE.IMAGE (IMAGE_REFERENCE, recipe_id, id) VALUES (:image, :recipe_id, nextval('recipe.image_seq'))");
        String pic = image.getImage();
        insertImage.setParameter("image", pic)
                .setParameter("recipe_id", recipeId)
                .executeUpdate();
    }
    private void updateFood(FoodEntity foodEntity) {
        Query updateFood = em.createNativeQuery("UPDATE RECIPE.FOOD SET unit = :unit, amount = :amount, food_id = :productId WHERE id = :id");
        updateFood.setParameter("unit", foodEntity.getUnit())
                .setParameter("amount", foodEntity.getAmount())
                .setParameter("productId", foodEntity.getProductEntity().getId())
                .setParameter("id", foodEntity.getId())
                .executeUpdate();
    }

    private void removeFood(List<FoodEntity> old, List<FoodEntity> fresh) {

        List<Long> aOld = new ArrayList<>();
        List<Long> aFresh = new ArrayList<>();

        for (FoodEntity f : old) {
            aOld.add(f.getId());
        }

        for (FoodEntity f : fresh) {
            aFresh.add(f.getId());
        }

        aOld.removeAll(aFresh);
        foodRepository.deleteFoodByListId(aOld);

    }


    private void removeStep(List<StepEntity> old, List<StepEntity> fresh) {
        List<Long> remove = new ArrayList<>();
        int diff = old.size() - fresh.size();
        if (diff > 0) {
            int oldSize = old.size();
            for (int i = 1; i <= diff; i++) {
                StepEntity tmp = old.get(oldSize - i);
                remove.add(tmp.getId());
            }
        }
        stepRepository.deleteFoodByListId(remove);
    }
}
