package ru.gubber.portfoliohistory.purchasedasset.service;

import org.springframework.stereotype.Service;
import ru.gubber.portfoliohistory.purchasedasset.model.AssetType;
import ru.gubber.portfoliohistory.purchasedasset.model.PurchasedAsset;
import ru.gubber.portfoliohistory.purchasedasset.repository.PurchasedAssetRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PurchasedAssetServiceImpl implements PurchasedAssetService {
    private final PurchasedAssetRepository repository;
    private static final double PURCHASE_PRICE = 1.0;
    private static final double CURRENT_PRICE = 1.0;

    public PurchasedAssetServiceImpl(PurchasedAssetRepository repository) {
        this.repository = repository;
    }

    @Override
    public void purchaseAsset(UUID accountId, String code, Double amount) {
        Optional<PurchasedAsset> optionalPurchasedAsset = repository.findByAccountIdAndCode(accountId, code);
        if (optionalPurchasedAsset.isEmpty()) {
            repository.save(new PurchasedAsset(UUID.randomUUID(), code, AssetType.CURRENCY, amount, PURCHASE_PRICE, CURRENT_PRICE, accountId));
        } else {
            PurchasedAsset asset = optionalPurchasedAsset.get();
            asset.setAmount(asset.getAmount() + amount);
            repository.save(asset);
        }
    }

    @Override
    public boolean sellAsset(UUID accountId, String code, Double amount) {
        Optional<PurchasedAsset> optionalPurchasedAsset = repository.findByAccountIdAndCode(accountId, code);
        if (optionalPurchasedAsset.isEmpty() || (optionalPurchasedAsset.get().getAmount() < amount)) {
            return false;
        }
        PurchasedAsset asset = optionalPurchasedAsset.get();
        asset.setAmount(asset.getAmount() + amount * -1);
        if (asset.getAmount() == 0) {
            repository.delete(asset);
            return true;
        }
        repository.save(asset);
        return true;
    }

    @Override
    public List<PurchasedAsset> getPurchasedAssetsList(UUID accountId) {
        return repository.findByAccountId(accountId);
    }
}
