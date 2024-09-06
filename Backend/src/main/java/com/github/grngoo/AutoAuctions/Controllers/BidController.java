package com.github.grngoo.AutoAuctions.Controllers;

import com.github.grngoo.AutoAuctions.DTOs.BidDto;
import com.github.grngoo.AutoAuctions.Models.Bid;
import com.github.grngoo.AutoAuctions.Models.Listing;
import com.github.grngoo.AutoAuctions.Models.Users;
import com.github.grngoo.AutoAuctions.Repositories.ListingRepository;
import com.github.grngoo.AutoAuctions.Repositories.UsersRepository;
import com.github.grngoo.AutoAuctions.Services.BidService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bid")
public class BidController {

    @Autowired
    private BidService bidService;

    /**
     * Search for all bids based on given dto filters.
     *
     * @param bidDto specify the attributes to filter by (listing or user)
     * @return listings matching filter (default return all).
     */
    @GetMapping("/search")
    public ResponseEntity<List<Bid>> searchBids(@Valid @RequestBody BidDto bidDto) {
        try {
            return new ResponseEntity<List<Bid>>(bidService.filterBids(bidDto), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Given a listing, find the highest bid for the listing
     *
     * @param bidDto specify the listingId
     * @return the bid entity with all details.
     */
    @GetMapping("/highest")
    public ResponseEntity<Bid> highestBid(@Valid @RequestBody BidDto bidDto) {
        try {
            Bid bid = bidService.findHighestBid(bidDto.getListingId());
            return new ResponseEntity<Bid>(bid, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Add a new bid for listing.
     *
     * @param bidDto All parameters for bid constructor.
     * @return new bid entity.
     */
    @PostMapping("/add")
    public ResponseEntity<Bid> addBid(@Valid @RequestBody BidDto bidDto) {
        try {
            return new ResponseEntity<Bid>(bidService.saveBid(bidDto), HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Delete a bid.
     *
     * @param bidDto contains the bidId for bid to be deleted.
     * @return Response status if deleted successfully.
     */
    @PostMapping("/delete")
    public ResponseEntity<String> deleteBid(@Valid @RequestBody BidDto bidDto) {
        try {
            Long bidId = bidDto.getBidId();
            bidService.deleteBid(bidId);
            return ResponseEntity.ok("Bid #" + bidId + " deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(409).body("Unable to delete bid due to invalid ID");
        }
    }
}
