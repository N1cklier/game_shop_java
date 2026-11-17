package gameshop.service;

import gameshop.exception.ShopException;

public interface ShopService {

    void processPurchase(int playerId, int itemId) throws ShopException;
}
