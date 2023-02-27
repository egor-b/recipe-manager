package com.foodcrunch.foodster.recipemanager.service;

import com.foodcrunch.foodster.recipemanager.model.PurchaseFoodResponse;
import com.foodcrunch.foodster.recipemanager.model.PurchaseResponse;
import com.foodcrunch.foodster.recipemanager.model.entity.FoodEntity;
import com.foodcrunch.foodster.recipemanager.model.entity.PurchaseEntity;
import com.foodcrunch.foodster.recipemanager.repository.FoodRepository;
import com.foodcrunch.foodster.recipemanager.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final FoodRepository foodRepository;

    public void deletePurchase(PurchaseEntity purchase) {
        purchaseRepository.deleteFromCart(purchase.getFood(), purchase.getRecipeId(), purchase.getUserId());
    }
    public void savePurchase(PurchaseEntity req) {
        purchaseRepository.save(req);

    }

    public Flux<PurchaseResponse> getPurchaseList(String userId) {
        int iter = 0;
        List<PurchaseResponse> responses = new ArrayList<>();
        List<PurchaseEntity> purchases = purchaseRepository.findByUserId(userId);
        for (PurchaseEntity p : purchases) {
            PurchaseResponse pr = new PurchaseResponse();
            pr.setServe(p.getServe());
            pr.setRecipeName(p.getRecipeName());
            pr.setRecipeId(p.getRecipeId());
            if (!responses.contains(pr)) {
                responses.add(pr);
            }
        }
        for (PurchaseResponse pr : responses) {
            List<PurchaseFoodResponse> lfe = new ArrayList<>();
            for (PurchaseEntity p : purchases) {
                if (pr.getRecipeId() == p.getRecipeId()) {
                    PurchaseFoodResponse pfr = new PurchaseFoodResponse();
                    FoodEntity fe = foodRepository.findById(p.getFood()).get();
                    pfr.setId(p.getId());
                    pfr.setName(fe.getProductEntity().getName());
                    pfr.setUnit(fe.getUnit());
                    pfr.setIsAvailable(p.isAvailable());
                    if (p.getAmount() != 0 ){
                        pfr.setAmount(String.valueOf(p.getAmount()));
                    } else {
                        pfr.setAmount(String.valueOf(fe.getAmount()));
                    }
                    lfe.add(pfr);
                }
            }
            responses.get(iter).setFood(lfe);
            iter ++;
        }
        return Flux.fromIterable(responses);
    }

    public PurchaseEntity getRecipePurchase(long foodId, long recipeId, String userId) {
        return purchaseRepository.findByFoodAndUserIdAndRecipeId(foodId, userId, recipeId);
    }

    public void updateCar(boolean isAdd, long id, String user) {
        purchaseRepository.updateCart(isAdd, id, user);
    }

    public void deleteFromCart(long foodId, long recipeId, String userId) {
        purchaseRepository.deleteFromCart(foodId, recipeId, userId);
    }
}
