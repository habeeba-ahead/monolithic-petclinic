#!/bin/bash

# Test script to demonstrate security functionality
# Run this after starting the application

echo "Testing Pet Clinic Security Implementation"
echo "=========================================="

BASE_URL="http://localhost:8080"

echo ""
echo "1. Testing public endpoint (should work):"
curl -s -o /dev/null -w "%{http_code}" $BASE_URL/
echo " - GET / returned: $(curl -s -o /dev/null -w "%{http_code}" $BASE_URL/)"

echo ""
echo "2. Testing management endpoint without authentication (should return 401):"
echo " - GET /management/revenue returned: $(curl -s -o /dev/null -w "%{http_code}" $BASE_URL/management/revenue)"

echo ""
echo "3. Testing management endpoint with user credentials (should return 403):"
echo " - GET /management/revenue with user:password returned: $(curl -s -o /dev/null -w "%{http_code}" -u user:password $BASE_URL/management/revenue)"

echo ""
echo "4. Testing management endpoint with admin credentials (should return 200):"
echo " - GET /management/revenue with admin:admin returned: $(curl -s -o /dev/null -w "%{http_code}" -u admin:admin $BASE_URL/management/revenue)"

echo ""
echo "5. Testing invalid credentials (should return 401):"
echo " - GET /management/revenue with admin:wrong returned: $(curl -s -o /dev/null -w "%{http_code}" -u admin:wrong $BASE_URL/management/revenue)"

echo ""
echo "Security test complete!"