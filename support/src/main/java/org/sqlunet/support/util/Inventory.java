/*
 * Copyright (c) 2019. Bernard Bou <1313ou@gmail.com>.
 */

package org.sqlunet.support.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Represents a block of information about in-app items.
 * An Inventory is returned by such methods as {@link IabHelper#queryInventory}.
 */
public class Inventory
{
	private final Map<String, SkuDetails> mSkuMap = new HashMap<>();
	private final Map<String, Purchase> mPurchaseMap = new HashMap<>();

	Inventory()
	{
	}

	/**
	 * Returns the listing details for an in-app product.
	 */
	@Nullable
	@SuppressWarnings("unused")
	public SkuDetails getSkuDetails(String sku)
	{
		return mSkuMap.get(sku);
	}

	/**
	 * Returns purchase information for a given product, or null if there is no purchase.
	 */
	@Nullable
	public Purchase getPurchase(String sku)
	{
		return mPurchaseMap.get(sku);
	}

	/**
	 * Returns whether or not there exists a purchase of the given product.
	 */
	@SuppressWarnings("unused")
	public boolean hasPurchase(String sku)
	{
		return mPurchaseMap.containsKey(sku);
	}

	/**
	 * Return whether or not details about the given product are available.
	 */
	@SuppressWarnings("unused")
	public boolean hasDetails(String sku)
	{
		return mSkuMap.containsKey(sku);
	}

	/**
	 * Erase a purchase (locally) from the inventory, given its product ID. This just
	 * modifies the Inventory object locally and has no effect on the server! This is
	 * useful when you have an existing Inventory object which you know to be up to date,
	 * and you have just consumed an item successfully, which means that erasing its
	 * purchase data from the Inventory you already have is quicker than querying for
	 * a new Inventory.
	 */
	@SuppressWarnings("unused")
	public void erasePurchase(String sku)
	{
		mPurchaseMap.remove(sku);
	}

	/**
	 * Returns a list of all owned product IDs.
	 */
	@NonNull
	@SuppressWarnings("unused")
	List<String> getAllOwnedSkus()
	{
		return new ArrayList<>(mPurchaseMap.keySet());
	}

	/**
	 * Returns a list of all owned product IDs of a given type
	 */
	@NonNull
	List<String> getAllOwnedSkus(String itemType)
	{
		List<String> result = new ArrayList<>();
		for (Purchase p : mPurchaseMap.values())
		{
			if (p.getItemType().equals(itemType))
			{
				result.add(p.getSku());
			}
		}
		return result;
	}

	/**
	 * Returns a list of all purchases.
	 */
	@NonNull
	@SuppressWarnings("unused")
	List<Purchase> getAllPurchases()
	{
		return new ArrayList<>(mPurchaseMap.values());
	}

	void addSkuDetails(@NonNull SkuDetails d)
	{
		mSkuMap.put(d.getSku(), d);
	}

	void addPurchase(@NonNull Purchase p)
	{
		mPurchaseMap.put(p.getSku(), p);
	}
}
