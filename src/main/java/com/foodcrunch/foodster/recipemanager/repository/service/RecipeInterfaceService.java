package com.foodcrunch.foodster.recipemanager.repository.service;

import com.foodcrunch.foodster.recipemanager.model.CookFoodEntity;
import com.foodcrunch.foodster.recipemanager.model.FoodstuffEntity;
import com.foodcrunch.foodster.recipemanager.model.Recipe;
import com.foodcrunch.foodster.recipemanager.repository.FoodRepository;
import com.foodcrunch.foodster.recipemanager.repository.RecipeInterface;
import com.foodcrunch.foodster.recipemanager.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Set;

@Service
public class RecipeInterfaceService implements RecipeInterface {

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    FoodRepository foodRepository;

    @PersistenceContext
    private EntityManager em;


    public Page<Recipe> findByPagingCriteria(Pageable pageable, Map<String, String> param) {
        Page page = recipeRepository.findAll(new Specification<Recipe>() {
            @Override
            public Predicate toPredicate(Root<Recipe> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

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
                    Root<CookFoodEntity> subCookFoodProject = subCookFood.from(CookFoodEntity.class);
                    subCookFood.select(subCookFoodProject.get("recipe"));
                    subCookFood.where(criteriaBuilder.and(subCookFoodProject.get("foodstuffEntity").in(subFoodstuff)));

                    predicates.add(criteriaBuilder.and(root.get("id").in(subCookFood)));
                } else if(/*param.get("minServe")!=null && */param.get("maxServe")!=null) {
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
    public void saveRecipe(Recipe recipe) {

        Query getRecipeSeq = em.createNativeQuery("SELECT nextval('fc.recipe_seq')");

        Query insertRecipe = em.createNativeQuery("INSERT INTO FC.RECIPE (about, date, language, level, name, serve, time, type, user_id, visible, id) VALUES (:about, :date, :language, :level, :name, :serve, :time, :type, :user_id, :visible, :id)");
        Query insertFoodstuff = em.createNativeQuery("INSERT INTO FC.FOODSTUFF (name, id) VALUES (:name, nextval('fc.foodstuff_seq'))");
        Query insertCookfood = em.createNativeQuery("INSERT INTO FC.COOKFOOD (image_link, food_id, measure, recipe_id, size, id) VALUES (:image_link, :food_id, :measure, :recipe_id, :size, nextval('fc.cookfood_seq'))");
        Query insertCookstep = em.createNativeQuery("INSERT INTO FC.COOKSTEP (image_link, step, step_number, recipe_id, id) VALUES (:image_link, :step, :step_number, :recipe_id, nextval('fc.cookstep_seq'))");
        Query insertImage = em.createNativeQuery("INSERT INTO FC.IMAGE (image_link, recipe_id, id) VALUES (:image_link, :recipe_id, nextval('fc.image_seq'))");
//Save main information about recipe
        BigInteger toLong = (BigInteger) getRecipeSeq.getSingleResult();

        recipe.setId(toLong.longValue());
        insertRecipe.setParameter("about", recipe.getAbout())
                .setParameter("date", recipe.getDate())
                .setParameter("language", recipe.getLang())
                .setParameter("level", recipe.getLevel())
                .setParameter("name", recipe.getName())
                .setParameter("serve", recipe.getServe())
                .setParameter("time", recipe.getTime())
                .setParameter("type", recipe.getType())
                .setParameter("user_id", recipe.getUser_id())
                .setParameter("visible", recipe.isVisible())
                .setParameter("id", recipe.getId()).executeUpdate();

//Save foodstuff or if exist get id and insert cook food
        Set<CookFoodEntity> food = recipe.getCookFoodEntity();
        for(CookFoodEntity f: food) {
            FoodstuffEntity resultFs;
            if (f.getFoodstuffEntity().getId()==0) {
                resultFs = foodRepository.findByNameEqualsIgnoreCase(f.getFoodstuffEntity().getName());
                if (resultFs == null) {
                    insertFoodstuff.setParameter("name", f.getFoodstuffEntity().getName()).executeUpdate();
                    resultFs = foodRepository.findByNameEqualsIgnoreCase(f.getFoodstuffEntity().getName());
                }
                f.setFoodstuffEntity(resultFs);
            }

            insertCookfood.setParameter("image_link",f.getImage())
                    .setParameter("food_id", f.getFoodstuffEntity().getId())
                    .setParameter("measure", f.getMeasure())
                    .setParameter("recipe_id", recipe.getId())
                    .setParameter("size", f.getSize())
                    .executeUpdate();

        }
//save cook steps
        recipe.getCookStepEntity().forEach((n) ->
                insertCookstep.setParameter("image_link", n.getImage())
                        .setParameter("step", n.getStep())
                        .setParameter("step_number", n.getStepId())
                        .setParameter("recipe_id", recipe.getId()).executeUpdate()
        );
// save links to dish images
        recipe.getImageEntity().forEach((n) ->
                insertImage.setParameter("image_link", n.getImage())
                        .setParameter("recipe_id", recipe.getId()).executeUpdate()
        );
    }
}
