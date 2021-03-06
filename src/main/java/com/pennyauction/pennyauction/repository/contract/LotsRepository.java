package com.pennyauction.pennyauction.repository.contract;

import com.pennyauction.pennyauction.model.Bid;
import com.pennyauction.pennyauction.model.Lot;
import com.pennyauction.pennyauction.model.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotsRepository {
    List<Lot> getLotsList();
    List<Lot> getLotsListByUser(String uid);
    Lot getLotById(int id);
    void updateLot(Lot lot);
    List<Bid> getBidsList(int lotId);
    int saveBid(Bid bid);
    int save(Lot lot, Product product);
}
