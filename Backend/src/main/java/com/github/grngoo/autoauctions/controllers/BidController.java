package com.github.grngoo.autoauctions.controllers;

import com.github.grngoo.autoauctions.dtos.BidDto;
import com.github.grngoo.autoauctions.models.Bid;
import com.github.grngoo.autoauctions.security.JwtUtility;
import com.github.grngoo.autoauctions.services.BidService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for endpoints related to bid functionality.
 *
 * @author danielmunteanu
 */
@RestController
@RequestMapping("/api/bid")
public class BidController {

  @Autowired private BidService bidService;

  @Autowired private JwtUtility jwtUtility;

  /**
   * Search for all bids based on given dto filters.
   *
   * @param bidDto specify the attributes to filter by (listing or user)
   * @return listings matching filter (default return all).
   */
  @GetMapping("/public/search")
  public ResponseEntity<List<Bid>> searchBids(@Valid @RequestBody BidDto bidDto) {
    try {
      return new ResponseEntity<>(bidService.filterBids(bidDto), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
  }

  /**
   * Given a listing, find the highest bid for the listing.
   *
   * @param bidDto specify the listingId
   * @return the bid entity with all details.
   */
  @GetMapping("/public/highest")
  public ResponseEntity<Bid> highestBid(@Valid @RequestBody BidDto bidDto) {
    try {
      Bid bid = bidService.findHighestBid(bidDto.getListingId());
      return new ResponseEntity<>(bid, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
  }

  /**
   * Add a new bid for listing. Only authorised users can place a bid under their own account.
   *
   * @param authorizationHeader contains the string for JWT token.
   * @param bidDto All parameters for bid constructor.
   * @return new bid entity.
   */
  @PostMapping("/add")
  public ResponseEntity<Bid> addBid(
      @RequestHeader("Authorization") String authorizationHeader,
      @Valid @RequestBody BidDto bidDto) {
    try {
      String token = authorizationHeader.substring(7);
      if (jwtUtility.validateToken(token, bidDto.getUsername())) {
        return new ResponseEntity<>(bidService.saveBid(bidDto), HttpStatus.OK);
      }
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
  }

  /**
   * Delete a specified bid. Only authorised users can delete their own bids.
   *
   * @param authorizationHeader contains the string for JWT token.
   * @param bidDto contains the bidId and user for bid to be deleted.
   * @return Response status if deleted successfully.
   */
  @PostMapping("/delete")
  public ResponseEntity<String> deleteBid(
      @RequestHeader("Authorization") String authorizationHeader,
      @Valid @RequestBody BidDto bidDto) {
    try {
      String token = authorizationHeader.substring(7);
      if (jwtUtility.validateToken(token, bidDto.getUsername())) {
        Long bidId = bidDto.getBidId();
        bidService.deleteBid(bidId);
        return ResponseEntity.ok("Bid #" + bidId + " deleted successfully");
      }
      return ResponseEntity.status(403).body("Unauthorised request, provide valid credentials");
    } catch (Exception e) {
      return ResponseEntity.status(409).body("Unable to delete bid due to invalid ID");
    }
  }
}
