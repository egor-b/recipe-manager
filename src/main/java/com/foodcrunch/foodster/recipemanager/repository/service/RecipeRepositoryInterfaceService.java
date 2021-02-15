package com.foodcrunch.foodster.recipemanager.repository.service;

import com.foodcrunch.foodster.recipemanager.model.entity.FoodEntity;
import com.foodcrunch.foodster.recipemanager.model.entity.FoodstuffEntity;
import com.foodcrunch.foodster.recipemanager.model.entity.RecipeEntity;
import com.foodcrunch.foodster.recipemanager.repository.FoodRepository;
import com.foodcrunch.foodster.recipemanager.repository.RecipeRepository;
import com.foodcrunch.foodster.recipemanager.repository.RecipeRepositoryInterface;
import com.foodcrunch.foodster.recipemanager.service.ImageManagerService;
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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecipeRepositoryInterfaceService implements RecipeRepositoryInterface {

    private final RecipeRepository recipeRepository;
    private final FoodRepository foodRepository;
    private final ImageManagerService imageManagerService;

    @PersistenceContext
    private EntityManager em;

    public Page<RecipeEntity> findByPagingCriteria(Pageable pageable, Map<String, String> param) {
        Page page = recipeRepository.findAll(new Specification<RecipeEntity>() {
            @Override
            public Predicate toPredicate(Root<RecipeEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicates = new ArrayList<>();
                if (param.get("name")!=null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("name"), "%" + param.get("name") + "%")));
                } else if (param.get("about")!=null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("about"), "%" + param.get("about") + "%")));
                } else if (param.get("type")!=null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("type"), param.get("type"))));
                } else if (param.get("user_id")!=null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("user_id"), param.get("userId"))));
                } else if (param.get("level")!=null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("level"), param.get("level"))));
                } else if (param.get("lang")!=null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("lang"), param.get("lang"))));
                } else if (param.get("ingredient")!=null) {
                    String[] separatedString = param.get("ingredient").split(",");

                    Subquery<Long> subFoodstuff = criteriaQuery.subquery(Long.class);
                    Root<FoodstuffEntity> subFoodstuffProject = subFoodstuff.from(FoodstuffEntity.class);
                    subFoodstuff.select(subFoodstuffProject.get("id"));

                    subFoodstuff.where(criteriaBuilder.and(subFoodstuffProject.get("name").in(separatedString)));

                    Subquery<Long> subCookFood = criteriaQuery.subquery(Long.class);
                    Root<FoodEntity> subCookFoodProject = subCookFood.from(FoodEntity.class);
                    subCookFood.select(subCookFoodProject.get("recipe"));
                    subCookFood.where(criteriaBuilder.and(subCookFoodProject.get("foodstuffEntity").in(subFoodstuff)));

                    predicates.add(criteriaBuilder.and(root.get("id").in(subCookFood)));
                } else if (/*param.get("minServe")!=null && */param.get("maxServe")!=null) {
//                        predicates.add(criteriaBuilder.between(
//                                root.get("serve"), Integer.valueOf(param.get("minServe")), Integer.valueOf(param.get("maxServe"))
//                        ));
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("serve"), Integer.valueOf(param.get("maxServe"))));
                } else if(param.get("minTime")!=null && param.get("maxTime")!=null) {
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
        Query insertFoodstuff = em.createNativeQuery("INSERT INTO RECIPE.FOODSTUFF (food_pic_byte, name, id) VALUES (:pic, :name, nextval('recipe.foodstuff_seq'))");
        Query insertFoodstuffNoPic = em.createNativeQuery("INSERT INTO RECIPE.FOODSTUFF (name, id) VALUES (:name, nextval('recipe.foodstuff_seq'))");

        Query insertFood = em.createNativeQuery("INSERT INTO RECIPE.FOOD (food_id, measure, recipe_id, size, id) VALUES (:food_id, :measure, :recipe_id, :size, nextval('recipe.cookfood_seq'))");
        Query insertStep = em.createNativeQuery("INSERT INTO RECIPE.STEP (cook_pic_byte, step, step_number, recipe_id, id) VALUES (:image, :step, :step_number, :recipe_id, nextval('recipe.cookstep_seq'))");
        Query insertImage = em.createNativeQuery("INSERT INTO RECIPE.IMAGE (pic_byte, recipe_id, id) VALUES (:image, :recipe_id, nextval('recipe.image_seq'))");
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
                .setParameter("visible", recipeEntity.isVisible())
                .setParameter("id", recipeEntity.getId())
                .executeUpdate();

        recipeEntity.getFoodEntity().forEach( (f) -> {
            if (f.getFoodstuffEntity().getId()==0) {
                FoodstuffEntity resultFs = foodRepository.findByNameEqualsIgnoreCase(f.getFoodstuffEntity().getName());
                if (resultFs == null) {
                    byte[] pic = imageManagerService.compressBytes(f.getFoodstuffEntity().getImage());
                    insertFoodstuff
                            .setParameter("name", f.getFoodstuffEntity().getName())
                            .setParameter("pic", pic)
                            .executeUpdate();
                    resultFs = foodRepository.findByNameEqualsIgnoreCase(f.getFoodstuffEntity().getName());
                }
                f.setFoodstuffEntity(resultFs);
            }

            insertFood
                    .setParameter("food_id", f.getFoodstuffEntity().getId())
                    .setParameter("measure", f.getMeasure())
                    .setParameter("recipe_id", recipeEntity.getId())
                    .setParameter("size", f.getSize())
                    .executeUpdate();
        });

//        recipeEntity.getStepEntity().forEach(n -> {
//            byte[] pic = imageManagerService.compressBytes(n.getImage());
//            insertStep
//                    .setParameter("image", pic)
//                    .setParameter("step", n.getStep())
//                    .setParameter("step_number", n.getStepNumber())
//                    .setParameter("recipe_id", recipeEntity.getId())
//                    .executeUpdate();
//        });
//
//        recipeEntity.getImageEntity().forEach(n -> {
//            byte[] pic = imageManagerService.compressBytes(n.getImage());
//            insertImage
//                    .setParameter("image", pic)
//                    .setParameter("recipe_id", recipeEntity.getId())
//                    .executeUpdate();
//        });
    }
}
