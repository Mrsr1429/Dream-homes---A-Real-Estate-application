package com.example.realestateapp.utils;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PropertyDataSeeder {
    private static final String TAG = "PropertyDataSeeder";
    private FirebaseFirestore db;
    private OnPropertiesAddedListener listener;

    public interface OnPropertiesAddedListener {
        void onPropertiesAdded(int totalCount, int successCount);
    }

    public PropertyDataSeeder() {
        db = FirebaseFirestore.getInstance();
    }

    public void setOnPropertiesAddedListener(OnPropertiesAddedListener listener) {
        this.listener = listener;
    }

    public void addSampleProperties() {
        // First add categories if they don't exist
        addCategories();
        
        // Then add properties
        List<Map<String, Object>> properties = getSampleProperties();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        int totalCount = properties.size();
        
        for (Map<String, Object> property : properties) {
            db.collection("Properties")
                    .add(property)
                    .addOnSuccessListener(documentReference -> {
                        int count = successCount.incrementAndGet();
                        Log.d(TAG, "Property added with ID: " + documentReference.getId() + " (" + count + "/" + totalCount + ")");
                        
                        // Notify listener when all properties are processed
                        if (count + failureCount.get() == totalCount && listener != null) {
                            listener.onPropertiesAdded(totalCount, count);
                        }
                    })
                    .addOnFailureListener(e -> {
                        int failCount = failureCount.incrementAndGet();
                        Log.e(TAG, "Error adding property", e);
                        
                        // Notify listener when all properties are processed
                        if (successCount.get() + failCount == totalCount && listener != null) {
                            listener.onPropertiesAdded(totalCount, successCount.get());
                        }
                    });
        }
    }

    private void addCategories() {
        // List of categories to add
        String[][] categories = {
            {"Home", "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=400"},
            {"Villa", "https://images.unsplash.com/photo-1613490493576-7fde63acd811?w=400"},
            {"Flat", "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=400"},
            {"Building", "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=400"},
            {"Bungalow", "https://images.unsplash.com/photo-1600607687920-4e2a09cf159d?w=400"},
            {"Farmhouse", "https://images.unsplash.com/photo-1600566753190-17f0baa2a74c?w=400"}
        };

        for (String[] categoryData : categories) {
            String categoryName = categoryData[0];
            String imageUrl = categoryData[1];
            
            Map<String, Object> category = new HashMap<>();
            category.put("title", categoryName);
            category.put("category", categoryName);
            category.put("imageuri", imageUrl);
            
            db.collection("Category")
                    .whereEqualTo("category", categoryName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().isEmpty()) {
                            db.collection("Category").add(category)
                                    .addOnSuccessListener(doc -> Log.d(TAG, categoryName + " category added"))
                                    .addOnFailureListener(e -> Log.e(TAG, "Error adding " + categoryName + " category", e));
                        }
                    });
        }
    }

    private List<Map<String, Object>> getSampleProperties() {
        List<Map<String, Object>> properties = new ArrayList<>();

        // Properties for SELLING
        properties.add(createProperty(
                "123 Oak Street, Downtown", 
                "₹3.75 Cr", 
                "Luxury 3-Bedroom Apartment",
                "Beautiful modern apartment in the heart of downtown. Features 3 spacious bedrooms, 2 bathrooms, open-concept living area with floor-to-ceiling windows, modern kitchen with stainless steel appliances, and a private balcony with city views. Building amenities include gym, pool, and 24/7 security.",
                "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800",
                "John Smith",
                "+1-555-0123",
                "Sell",
                "Home"
        ));

        properties.add(createProperty(
                "456 Maple Avenue, Riverside",
                "₹7.08 Cr",
                "Spacious 4-Bedroom Villa",
                "Stunning villa with 4 bedrooms and 3.5 bathrooms. Features include a gourmet kitchen, large living room with fireplace, master suite with walk-in closet, private garden, and 2-car garage. Located in a quiet residential area with excellent schools nearby.",
                "https://images.unsplash.com/photo-1613490493576-7fde63acd811?w=800",
                "Sarah Johnson",
                "+1-555-0124",
                "Sell",
                "Villa"
        ));

        properties.add(createProperty(
                "789 Pine Road, Hillside",
                "₹2.67 Cr",
                "Cozy 2-Bedroom Flat",
                "Charming 2-bedroom flat in a well-maintained building. Features include updated kitchen, hardwood floors, large windows, and a small balcony. Perfect for first-time buyers or small families. Close to public transportation and shopping centers.",
                "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800",
                "Michael Brown",
                "+1-555-0125",
                "Sell",
                "Flat"
        ));

        properties.add(createProperty(
                "321 Elm Street, Parkview",
                "₹10 Cr",
                "Elegant 5-Bedroom Mansion",
                "Magnificent 5-bedroom mansion with 4 bathrooms. Features include grand entrance, formal dining room, home office, media room, wine cellar, outdoor pool, and landscaped gardens. Perfect for large families or entertaining guests.",
                "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=800",
                "Emily Davis",
                "+1-555-0126",
                "Sell",
                "Villa"
        ));

        properties.add(createProperty(
                "654 Cedar Lane, Waterfront",
                "₹5.67 Cr",
                "Modern 3-Bedroom Condo",
                "Contemporary 3-bedroom condo with waterfront views. Features include open floor plan, modern kitchen with island, master bedroom with ensuite, balcony overlooking the water, and access to community amenities including marina and clubhouse.",
                "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=800",
                "Robert Wilson",
                "+1-555-0127",
                "Sell",
                "Home"
        ));

        properties.add(createProperty(
                "987 Birch Boulevard, Suburbia",
                "₹2.29 Cr",
                "Affordable 2-Bedroom Apartment",
                "Well-maintained 2-bedroom apartment in a family-friendly neighborhood. Features include updated bathroom, kitchen with appliances, ample storage, and assigned parking. Great investment property or starter home.",
                "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800",
                "Lisa Anderson",
                "+1-555-0128",
                "Sell",
                "Flat"
        ));

        // Properties for RENTING
        properties.add(createProperty(
                "111 Main Street, City Center",
                "₹2,500/month",
                "Furnished 2-Bedroom Apartment",
                "Fully furnished modern 2-bedroom apartment in prime location. Includes all utilities, high-speed internet, cable TV, and access to building amenities. Perfect for professionals or students. Available immediately.",
                "https://images.unsplash.com/photo-1505843513577-22bb7d21e455?w=800",
                "David Martinez",
                "+1-555-0129",
                "Rent",
                "Home"
        ));

        properties.add(createProperty(
                "222 Park Avenue, Uptown",
                "₹3,800/month",
                "Luxury 3-Bedroom Penthouse",
                "Stunning 3-bedroom penthouse with panoramic city views. Features include premium finishes, gourmet kitchen, spacious living area, private terrace, and access to rooftop pool and gym. Perfect for executives or families seeking luxury living.",
                "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800",
                "Jennifer Lee",
                "+1-555-0130",
                "Rent",
                "Villa"
        ));

        properties.add(createProperty(
                "333 Garden Lane, Greenfield",
                "₹1,800/month",
                "Charming 1-Bedroom Studio",
                "Cozy 1-bedroom studio apartment in a quiet residential area. Features include modern kitchenette, updated bathroom, large windows, and access to shared garden. Ideal for single professionals or couples. Pet-friendly building.",
                "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800",
                "James Taylor",
                "+1-555-0131",
                "Rent",
                "Flat"
        ));

        properties.add(createProperty(
                "444 Sunset Drive, Beachside",
                "₹4,200/month",
                "Beachfront 4-Bedroom Villa",
                "Spectacular beachfront villa with 4 bedrooms and 3 bathrooms. Features include ocean views from every room, private beach access, outdoor dining area, fully equipped kitchen, and modern amenities. Perfect for vacation rental or long-term lease.",
                "https://images.unsplash.com/photo-1600607687920-4e2a09cf159d?w=800",
                "Amanda White",
                "+1-555-0132",
                "Rent",
                "Villa"
        ));

        properties.add(createProperty(
                "555 Mountain View Road, Highlands",
                "₹2,200/month",
                "Spacious 3-Bedroom Townhouse",
                "Comfortable 3-bedroom townhouse with 2.5 bathrooms. Features include attached garage, private yard, modern kitchen, and family room. Located in a safe neighborhood with excellent schools. Available for long-term lease.",
                "https://images.unsplash.com/photo-1600566753190-17f0baa2a74c?w=800",
                "Christopher Harris",
                "+1-555-0133",
                "Rent",
                "Home"
        ));

        properties.add(createProperty(
                "666 Commerce Street, Business District",
                "₹1,500/month",
                "Modern 1-Bedroom Loft",
                "Contemporary 1-bedroom loft in converted warehouse. Features include high ceilings, exposed brick walls, modern appliances, and open floor plan. Walking distance to restaurants, shops, and public transportation. Perfect for young professionals.",
                "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800",
                "Nicole Garcia",
                "+1-555-0134",
                "Rent",
                "Flat"
        ));

        properties.add(createProperty(
                "777 Lakeview Circle, Lakeside",
                "₹3,500/month",
                "Elegant 3-Bedroom House",
                "Beautiful 3-bedroom house with lake views. Features include large backyard, updated kitchen, hardwood floors, fireplace, and 2-car garage. Quiet neighborhood with easy access to parks and recreational facilities. Great for families.",
                "https://images.unsplash.com/photo-1600585152915-d0bec72a0df0?w=800",
                "Daniel Rodriguez",
                "+1-555-0135",
                "Rent",
                "Home"
        ));

        properties.add(createProperty(
                "888 Forest Trail, Woodland",
                "₹2,800/month",
                "Rustic 2-Bedroom Cabin",
                "Charming 2-bedroom cabin in wooded setting. Features include stone fireplace, wrap-around porch, modern kitchen, and peaceful surroundings. Perfect for nature lovers seeking a quiet retreat. Short drive to city center.",
                "https://images.unsplash.com/photo-1600607687644-c7171b42498b?w=800",
                "Michelle Clark",
                "+1-555-0136",
                "Rent",
                "Home"
        ));

        properties.add(createProperty(
                "999 Skyline Boulevard, Downtown",
                "₹5,000/month",
                "Premium 4-Bedroom Penthouse",
                "Exclusive 4-bedroom penthouse with stunning skyline views. Features include premium finishes throughout, chef's kitchen, home theater, wine cellar, and private elevator access. Building amenities include concierge, valet parking, and rooftop terrace.",
                "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800",
                "Andrew Lewis",
                "+1-555-0137",
                "Rent",
                "Villa"
        ));

        properties.add(createProperty(
                "1010 Valley Road, Countryside",
                "₹1,200/month",
                "Cozy 1-Bedroom Cottage",
                "Quaint 1-bedroom cottage with character. Features include original hardwood floors, updated bathroom, small garden, and off-street parking. Peaceful location perfect for those seeking a quiet lifestyle away from the city hustle.",
                "https://images.unsplash.com/photo-1600566753086-00f18fb6b3ea?w=800",
                "Stephanie Walker",
                "+1-555-0138",
                "Rent",
                "Home"
        ));

        // ========== BUILDINGS FOR SELLING ==========
        properties.add(createProperty(
                "2000 Business Plaza, Financial District",
                "₹20.83 Cr",
                "Modern 8-Story Commercial Building",
                "Prime commercial building with 8 floors, perfect for offices, retail, or mixed-use. Features include modern elevators, central HVAC, parking garage, rooftop terrace, and prime location in the financial district. Excellent investment opportunity with high rental potential.",
                "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=800",
                "Richard Thompson",
                "+1-555-0201",
                "Sell",
                "Building"
        ));

        properties.add(createProperty(
                "1500 Industrial Way, Manufacturing Zone",
                "₹15 Cr",
                "Large Warehouse Building",
                "Spacious warehouse building with 50,000 sq ft of space. Features include high ceilings, loading docks, office space, and excellent access to major highways. Ideal for manufacturing, storage, or distribution business.",
                "https://images.unsplash.com/photo-1582407947304-fd86f028f716?w=800",
                "Patricia Moore",
                "+1-555-0202",
                "Sell",
                "Building"
        ));

        properties.add(createProperty(
                "3000 Residential Tower, Downtown",
                "₹35 Cr",
                "12-Story Apartment Building",
                "Well-maintained 12-story apartment building with 48 units. Features include modern amenities, elevator access, on-site management, parking, and prime downtown location. Excellent rental income potential with high occupancy rates.",
                "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800",
                "Thomas Jackson",
                "+1-555-0203",
                "Sell",
                "Building"
        ));

        properties.add(createProperty(
                "2500 Retail Center, Shopping District",
                "₹29.17 Cr",
                "Multi-Unit Retail Building",
                "Prime retail building with 15 storefronts and 2 floors. Features include high foot traffic location, modern storefronts, parking lot, and excellent visibility. Perfect for retail businesses or investment property.",
                "https://images.unsplash.com/photo-1449844908441-8829872d2607?w=800",
                "Barbara Taylor",
                "+1-555-0204",
                "Sell",
                "Building"
        ));

        properties.add(createProperty(
                "1800 Office Complex, Business Park",
                "₹23.33 Cr",
                "Modern Office Building",
                "Contemporary 6-story office building with 120,000 sq ft of leasable space. Features include state-of-the-art facilities, conference rooms, parking garage, and modern design. Ideal for corporate headquarters or multi-tenant office space.",
                "https://images.unsplash.com/photo-1497366216548-37526070297c?w=800",
                "William Anderson",
                "+1-555-0205",
                "Sell",
                "Building"
        ));

        // ========== BUILDINGS FOR RENTING ==========
        properties.add(createProperty(
                "2200 Storefront Avenue, Main Street",
                "₹8,500/month",
                "Prime Retail Space Building",
                "Excellent retail space in high-traffic area. Features include large storefront windows, modern interior, storage area, and prime location on main street. Perfect for retail business, restaurant, or service provider.",
                "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=800",
                "Margaret Wilson",
                "+1-555-0206",
                "Rent",
                "Building"
        ));

        properties.add(createProperty(
                "2700 Office Tower, Corporate Center",
                "₹12,000/month",
                "Premium Office Space Building",
                "Luxury office space in prestigious building. Features include modern design, conference facilities, reception area, parking, and access to building amenities. Ideal for professional services, law firms, or corporate offices.",
                "https://images.unsplash.com/photo-1497366754035-f200968a6e72?w=800",
                "Joseph Martin",
                "+1-555-0207",
                "Rent",
                "Building"
        ));

        properties.add(createProperty(
                "1900 Warehouse District, Industrial Area",
                "₹6,500/month",
                "Large Warehouse Space",
                "Spacious warehouse facility with 25,000 sq ft. Features include high ceilings, loading docks, office space, and excellent access. Perfect for storage, distribution, or light manufacturing business.",
                "https://images.unsplash.com/photo-1582407947304-fd86f028f716?w=800",
                "Susan Davis",
                "+1-555-0208",
                "Rent",
                "Building"
        ));

        // ========== BUNGALOWS FOR SELLING ==========
        properties.add(createProperty(
                "1200 Seaside Drive, Coastal Area",
                "₹5.42 Cr",
                "Charming Beachfront Bungalow",
                "Beautiful beachfront bungalow with direct ocean access. Features include 3 bedrooms, 2 bathrooms, open living area, large deck, and stunning ocean views. Perfect for vacation home or year-round living in a peaceful coastal setting.",
                "https://images.unsplash.com/photo-1600607687920-4e2a09cf159d?w=800",
                "Robert Green",
                "+1-555-0301",
                "Sell",
                "Bungalow"
        ));

        properties.add(createProperty(
                "1300 Garden Path, Suburban",
                "₹4 Cr",
                "Classic 2-Bedroom Bungalow",
                "Charming single-story bungalow with 2 bedrooms and 1.5 bathrooms. Features include covered front porch, hardwood floors, updated kitchen, large backyard, and quiet neighborhood. Perfect starter home or retirement property.",
                "https://images.unsplash.com/photo-1600607687644-c7171b42498b?w=800",
                "Linda Brown",
                "+1-555-0302",
                "Sell",
                "Bungalow"
        ));

        properties.add(createProperty(
                "1400 Country Lane, Rural",
                "₹3.5 Cr",
                "Spacious 4-Bedroom Bungalow",
                "Large family bungalow with 4 bedrooms and 3 bathrooms. Features include single-story living, large kitchen, family room, wrap-around porch, and 2-acre lot. Ideal for families seeking single-level living with space and privacy.",
                "https://images.unsplash.com/photo-1600566753190-17f0baa2a74c?w=800",
                "Mark Johnson",
                "+1-555-0303",
                "Sell",
                "Bungalow"
        ));

        properties.add(createProperty(
                "1500 Heritage Street, Historic District",
                "₹4.83 Cr",
                "Restored Vintage Bungalow",
                "Beautifully restored 1920s bungalow with original character. Features include 3 bedrooms, 2 bathrooms, original hardwood floors, period details, modern updates, and large front porch. Located in historic district with tree-lined streets.",
                "https://images.unsplash.com/photo-1600607687920-4e2a09cf159d?w=800",
                "Karen White",
                "+1-555-0304",
                "Sell",
                "Bungalow"
        ));

        properties.add(createProperty(
                "1600 Mountain View, Hillside",
                "₹6 Cr",
                "Modern Luxury Bungalow",
                "Contemporary bungalow with mountain views. Features include 3 bedrooms, 2.5 bathrooms, open floor plan, modern kitchen, master suite, deck, and landscaped yard. Modern amenities with single-story convenience.",
                "https://images.unsplash.com/photo-1600585152915-d0bec72a0df0?w=800",
                "Steven Miller",
                "+1-555-0305",
                "Sell",
                "Bungalow"
        ));

        properties.add(createProperty(
                "1700 Lakefront Road, Waterfront",
                "₹7.42 Cr",
                "Waterfront Bungalow with Dock",
                "Stunning waterfront bungalow with private dock. Features include 3 bedrooms, 2 bathrooms, large windows with lake views, screened porch, boat dock, and peaceful setting. Perfect for water enthusiasts or peaceful retirement.",
                "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=800",
                "Nancy Garcia",
                "+1-555-0306",
                "Sell",
                "Bungalow"
        ));

        properties.add(createProperty(
                "1800 Cottage Lane, Countryside",
                "₹3.17 Cr",
                "Cozy 2-Bedroom Bungalow",
                "Adorable 2-bedroom bungalow in quiet countryside setting. Features include covered porch, cozy living room, updated kitchen, large yard, and peaceful surroundings. Great for first-time buyers or downsizing.",
                "https://images.unsplash.com/photo-1600566753086-00f18fb6b3ea?w=800",
                "Paul Martinez",
                "+1-555-0307",
                "Sell",
                "Bungalow"
        ));

        // ========== BUNGALOWS FOR RENTING ==========
        properties.add(createProperty(
                "2100 Beach Bungalow Lane, Coastal",
                "₹3,200/month",
                "Beachside Bungalow Rental",
                "Charming beachside bungalow perfect for vacation or long-term rental. Features include 2 bedrooms, 1 bathroom, beach access, outdoor shower, and relaxed coastal living. Short walk to beach, restaurants, and shops.",
                "https://images.unsplash.com/photo-1600607687920-4e2a09cf159d?w=800",
                "Carol Robinson",
                "+1-555-0401",
                "Rent",
                "Bungalow"
        ));

        properties.add(createProperty(
                "2300 Garden Bungalow, Residential",
                "₹2,100/month",
                "Furnished Garden Bungalow",
                "Fully furnished 2-bedroom bungalow with beautiful garden. Features include covered porch, modern kitchen, comfortable living area, private yard, and quiet neighborhood. Perfect for professionals or small families.",
                "https://images.unsplash.com/photo-1600607687644-c7171b42498b?w=800",
                "Kevin Lee",
                "+1-555-0402",
                "Rent",
                "Bungalow"
        ));

        properties.add(createProperty(
                "2400 Family Bungalow, Suburban",
                "₹2,800/month",
                "Spacious 3-Bedroom Bungalow",
                "Large 3-bedroom bungalow ideal for families. Features include single-story living, large kitchen, family room, fenced yard, and excellent schools nearby. Quiet neighborhood with parks and amenities close by.",
                "https://images.unsplash.com/photo-1600566753190-17f0baa2a74c?w=800",
                "Donna Clark",
                "+1-555-0403",
                "Rent",
                "Bungalow"
        ));

        properties.add(createProperty(
                "2600 Retreat Bungalow, Countryside",
                "₹1,900/month",
                "Peaceful Country Bungalow",
                "Quiet countryside bungalow for those seeking tranquility. Features include 2 bedrooms, large yard, covered porch, and peaceful setting. Perfect for remote workers or those wanting to escape city life.",
                "https://images.unsplash.com/photo-1600566753086-00f18fb6b3ea?w=800",
                "Brian Lewis",
                "+1-555-0404",
                "Rent",
                "Bungalow"
        ));

        properties.add(createProperty(
                "2800 Modern Bungalow, Urban",
                "₹2,500/month",
                "Updated Urban Bungalow",
                "Recently updated bungalow in urban setting. Features include modern finishes, 2 bedrooms, updated kitchen and bathroom, small yard, and convenient location. Close to public transportation and city amenities.",
                "https://images.unsplash.com/photo-1600585152915-d0bec72a0df0?w=800",
                "Ruth Walker",
                "+1-555-0405",
                "Rent",
                "Bungalow"
        ));

        properties.add(createProperty(
                "2900 Lakeside Bungalow, Waterfront",
                "₹3,600/month",
                "Luxury Lakefront Bungalow",
                "Premium lakefront bungalow with stunning views. Features include 3 bedrooms, 2 bathrooms, large deck, lake access, and modern amenities. Perfect for those seeking waterfront living with single-story convenience.",
                "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=800",
                "Gary Hall",
                "+1-555-0406",
                "Rent",
                "Bungalow"
        ));

        // ========== ADDITIONAL BUILDINGS ==========
        properties.add(createProperty(
                "3100 Medical Plaza, Healthcare District",
                "₹12.5 Cr",
                "Medical Office Building",
                "Specialized medical office building with 5 floors. Features include modern exam rooms, waiting areas, parking, and prime location near hospital. Ideal for medical practices, clinics, or healthcare services.",
                "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=800",
                "Patricia Young",
                "+1-555-0501",
                "Sell",
                "Building"
        ));

        properties.add(createProperty(
                "3200 Student Housing, University Area",
                "₹18.33 Cr",
                "Student Apartment Building",
                "Purpose-built student housing with 60 units. Features include furnished apartments, study areas, common spaces, and walking distance to university. Excellent investment with consistent rental demand.",
                "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800",
                "Edward King",
                "+1-555-0502",
                "Sell",
                "Building"
        ));

        properties.add(createProperty(
                "3300 Mixed-Use Building, Downtown",
                "₹31.67 Cr",
                "Residential-Commercial Building",
                "Versatile mixed-use building with retail on ground floor and apartments above. Features include 8 retail units and 24 residential units, prime downtown location, and excellent rental income potential.",
                "https://images.unsplash.com/photo-1449844908441-8829872d2607?w=800",
                "Betty Wright",
                "+1-555-0503",
                "Sell",
                "Building"
        ));

        properties.add(createProperty(
                "3400 Storage Facility, Industrial",
                "₹7.92 Cr",
                "Self-Storage Building",
                "Well-established self-storage facility with 200 units. Features include climate-controlled units, security system, office space, and excellent location. Proven business with steady income stream.",
                "https://images.unsplash.com/photo-1582407947304-fd86f028f716?w=800",
                "Raymond Lopez",
                "+1-555-0504",
                "Sell",
                "Building"
        ));

        properties.add(createProperty(
                "3500 Restaurant Space, Entertainment District",
                "₹5,500/month",
                "Prime Restaurant Building",
                "Perfect restaurant space in entertainment district. Features include large dining area, commercial kitchen, bar area, outdoor patio, and high foot traffic location. Turnkey setup ready for restaurant operation.",
                "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=800",
                "Sharon Hill",
                "+1-555-0505",
                "Rent",
                "Building"
        ));

        // ========== ADDITIONAL BUNGALOWS ==========
        properties.add(createProperty(
                "3600 Craftsman Bungalow, Arts District",
                "₹4.33 Cr",
                "Arts & Crafts Style Bungalow",
                "Authentic Craftsman bungalow with period details. Features include 3 bedrooms, built-in cabinetry, fireplace, covered porch, and character throughout. Located in vibrant arts district with galleries and cafes nearby.",
                "https://images.unsplash.com/photo-1600607687920-4e2a09cf159d?w=800",
                "Frank Scott",
                "+1-555-0601",
                "Sell",
                "Bungalow"
        ));

        properties.add(createProperty(
                "3700 Ranch Bungalow, Suburban",
                "₹3.75 Cr",
                "Single-Story Ranch Bungalow",
                "Spacious ranch-style bungalow with 3 bedrooms and 2 bathrooms. Features include open floor plan, large lot, attached garage, and family-friendly neighborhood. Perfect for families or retirees.",
                "https://images.unsplash.com/photo-1600566753190-17f0baa2a74c?w=800",
                "Deborah Green",
                "+1-555-0602",
                "Sell",
                "Bungalow"
        ));

        properties.add(createProperty(
                "3800 Vacation Bungalow, Resort Area",
                "₹2,400/month",
                "Furnished Vacation Bungalow",
                "Fully furnished vacation bungalow perfect for short-term or seasonal rental. Features include 2 bedrooms, modern amenities, outdoor space, and access to resort facilities. Great for vacationers or seasonal residents.",
                "https://images.unsplash.com/photo-1600607687644-c7171b42498b?w=800",
                "Gregory Adams",
                "+1-555-0603",
                "Rent",
                "Bungalow"
        ));

        properties.add(createProperty(
                "3900 Eco-Friendly Bungalow, Green Community",
                "₹5.67 Cr",
                "Sustainable Green Bungalow",
                "Eco-friendly bungalow with solar panels and sustainable features. Features include 3 bedrooms, energy-efficient design, rainwater collection, organic garden, and modern green technology. Perfect for environmentally conscious buyers.",
                "https://images.unsplash.com/photo-1600585152915-d0bec72a0df0?w=800",
                "Kimberly Baker",
                "+1-555-0604",
                "Sell",
                "Bungalow"
        ));

        properties.add(createProperty(
                "4000 Senior Living Bungalow, Retirement Community",
                "₹2,200/month",
                "Accessible Senior Bungalow",
                "Designed for senior living with accessibility features. Features include single-story layout, grab bars, wide doorways, low-maintenance yard, and access to community amenities. Perfect for active seniors.",
                "https://images.unsplash.com/photo-1600566753086-00f18fb6b3ea?w=800",
                "Jerry Nelson",
                "+1-555-0605",
                "Rent",
                "Bungalow"
        ));

        // ========== FARMHOUSES FOR SELLING ==========
        properties.add(createProperty(
                "5000 Country Estate, Rural Area",
                "₹8.5 Cr",
                "Luxury Farmhouse Estate",
                "Magnificent farmhouse estate on 10 acres. Features include 5 bedrooms, 4 bathrooms, large living areas, modern kitchen, swimming pool, stables, and beautiful gardens. Perfect for those seeking country living with luxury amenities.",
                "https://images.unsplash.com/photo-1600566753190-17f0baa2a74c?w=800",
                "Rajesh Kumar",
                "+91-9876543210",
                "Sell",
                "Farmhouse"
        ));

        properties.add(createProperty(
                "5100 Heritage Farmhouse, Countryside",
                "₹6.25 Cr",
                "Traditional Farmhouse",
                "Beautiful traditional farmhouse with 4 bedrooms and 3 bathrooms. Features include original architecture, large verandah, farm land, and peaceful countryside setting. Ideal for those seeking authentic rural living.",
                "https://images.unsplash.com/photo-1600607687644-c7171b42498b?w=800",
                "Priya Sharma",
                "+91-9876543211",
                "Sell",
                "Farmhouse"
        ));

        properties.add(createProperty(
                "5200 Modern Farmhouse, Suburban",
                "₹4.5 Cr",
                "Contemporary Farmhouse",
                "Modern farmhouse with 3 bedrooms and 2.5 bathrooms. Features include contemporary design, open floor plan, large windows, landscaped gardens, and proximity to city. Best of both worlds - country charm with modern convenience.",
                "https://images.unsplash.com/photo-1600585152915-d0bec72a0df0?w=800",
                "Amit Patel",
                "+91-9876543212",
                "Sell",
                "Farmhouse"
        ));

        properties.add(createProperty(
                "5300 Organic Farmhouse, Green Valley",
                "₹5.75 Cr",
                "Eco-Friendly Farmhouse",
                "Sustainable farmhouse with organic farm. Features include 4 bedrooms, solar panels, rainwater harvesting, organic vegetable garden, and eco-friendly construction. Perfect for environmentally conscious buyers.",
                "https://images.unsplash.com/photo-1600566753190-17f0baa2a74c?w=800",
                "Sunita Reddy",
                "+91-9876543213",
                "Sell",
                "Farmhouse"
        ));

        // ========== FARMHOUSES FOR RENTING ==========
        properties.add(createProperty(
                "5400 Weekend Farmhouse, Hill Station",
                "₹25,000/month",
                "Holiday Farmhouse Rental",
                "Charming farmhouse perfect for weekend getaways. Features include 3 bedrooms, fireplace, large garden, mountain views, and peaceful surroundings. Ideal for families seeking a break from city life.",
                "https://images.unsplash.com/photo-1600607687644-c7171b42498b?w=800",
                "Vikram Singh",
                "+91-9876543214",
                "Rent",
                "Farmhouse"
        ));

        properties.add(createProperty(
                "5500 Family Farmhouse, Countryside",
                "₹35,000/month",
                "Spacious Family Farmhouse",
                "Large farmhouse ideal for extended families. Features include 5 bedrooms, 3 bathrooms, large kitchen, dining hall, courtyard, and farm land. Perfect for large families or group rentals.",
                "https://images.unsplash.com/photo-1600566753190-17f0baa2a74c?w=800",
                "Meera Joshi",
                "+91-9876543215",
                "Rent",
                "Farmhouse"
        ));

        properties.add(createProperty(
                "5600 Rustic Farmhouse, Village",
                "₹18,000/month",
                "Traditional Village Farmhouse",
                "Authentic village farmhouse with traditional architecture. Features include 2 bedrooms, courtyard, well, and agricultural land. Perfect for those seeking authentic rural experience.",
                "https://images.unsplash.com/photo-1600566753086-00f18fb6b3ea?w=800",
                "Anil Verma",
                "+91-9876543216",
                "Rent",
                "Farmhouse"
        ));

        return properties;
    }

    private Map<String, Object> createProperty(
            String location,
            String price,
            String shortDescription,
            String description,
            String imageUri,
            String ownerName,
            String contactNo,
            String type,
            String category) {
        
        Map<String, Object> property = new HashMap<>();
        property.put("location", location);
        property.put("price", price);
        property.put("shortdescription", shortDescription);
        property.put("description", description);
        property.put("imageuri", imageUri);
        property.put("ownername", ownerName);
        property.put("contactno", contactNo);
        property.put("type", type);
        property.put("category", category);
        
        return property;
    }
}
