import React, { useState } from 'react';
import {
  Container,
  Typography,
  Grid,
  Card,
  CardMedia,
  CardContent,
  CardActions,
  Button,
  Box,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Pagination,
  CircularProgress,
  Alert,
} from '@mui/material';
import { AddShoppingCart } from '@mui/icons-material';
import { Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { useContext } from 'react';
import { CartContext } from '../contexts/CartContext';
import apiClient, { API_ENDPOINTS } from '../config/api';
import toast from 'react-hot-toast';

const Products = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [category, setCategory] = useState('');
  const [sortBy, setSortBy] = useState('name');
  const [page, setPage] = useState(1);
  const pageSize = 12;

  const { addToCart } = useContext(CartContext);

  // Fetch products
  const {
    data: productsData,
    isLoading: productsLoading,
    error: productsError,
  } = useQuery({
    queryKey: ['products', page, searchTerm, category, sortBy],
    queryFn: async () => {
      const params = new URLSearchParams({
        page: page - 1, // Backend expects 0-based page
        size: pageSize,
        sort: sortBy,
      });
      
      if (searchTerm) params.append('search', searchTerm);
      if (category) params.append('category', category);
      
      const response = await apiClient.get(`${API_ENDPOINTS.PRODUCTS}?${params}`);
      return response.data;
    },
  });

  // Fetch categories
  const {
    data: categories,
    isLoading: categoriesLoading,
  } = useQuery({
    queryKey: ['categories'],
    queryFn: async () => {
      const response = await apiClient.get(API_ENDPOINTS.PRODUCT_CATEGORIES);
      return response.data;
    },
  });

  const handleAddToCart = (product) => {
    addToCart(product);
    toast.success(`${product.name} added to cart!`);
  };

  const handleSearch = (event) => {
    setSearchTerm(event.target.value);
    setPage(1); // Reset to first page when searching
  };

  const handleCategoryChange = (event) => {
    setCategory(event.target.value);
    setPage(1);
  };

  const handleSortChange = (event) => {
    setSortBy(event.target.value);
    setPage(1);
  };

  const handlePageChange = (event, newPage) => {
    setPage(newPage);
  };

  if (productsError) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Alert severity="error">
          Failed to load products. Please try again later.
        </Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: { xs: 2, md: 4 } }}>
      <Typography 
        variant="h4" 
        component="h1" 
        gutterBottom
        sx={{ fontSize: { xs: '1.5rem', sm: '2rem', md: '2.5rem' } }}
      >
        Products
      </Typography>

      {/* Filters and Search */}
      <Box sx={{ mb: { xs: 3, md: 4 } }}>
        <Grid container spacing={{ xs: 1, sm: 2 }} alignItems="center">
          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              label="Search products..."
              variant="outlined"
              value={searchTerm}
              onChange={handleSearch}
              size="small"
              sx={{ mb: { xs: 1, md: 0 } }}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <FormControl fullWidth size="small">
              <InputLabel>Category</InputLabel>
              <Select
                value={category}
                label="Category"
                onChange={handleCategoryChange}
                disabled={categoriesLoading}
              >
                <MenuItem value="">All Categories</MenuItem>
                {categories?.map((cat) => (
                  <MenuItem key={cat.id} value={cat.name}>
                    {cat.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <FormControl fullWidth size="small">
              <InputLabel>Sort By</InputLabel>
              <Select
                value={sortBy}
                label="Sort By"
                onChange={handleSortChange}
              >
                <MenuItem value="name">Name (A-Z)</MenuItem>
                <MenuItem value="name,desc">Name (Z-A)</MenuItem>
                <MenuItem value="price">Price (Low to High)</MenuItem>
                <MenuItem value="price,desc">Price (High to Low)</MenuItem>
                <MenuItem value="createdAt,desc">Newest First</MenuItem>
              </Select>
            </FormControl>
          </Grid>
        </Grid>
      </Box>

      {/* Products Grid */}
      {productsLoading ? (
        <Box display="flex" justifyContent="center" py={4}>
          <CircularProgress />
        </Box>
      ) : (
        <>
          <Grid container spacing={{ xs: 2, md: 3 }}>
            {productsData?.content?.map((product) => (
              <Grid item xs={6} sm={4} md={3} lg={3} key={product.id}>
                <Card
                  sx={{
                    height: '100%',
                    display: 'flex',
                    flexDirection: 'column',
                    transition: 'all 0.3s ease-in-out',
                    '&:hover': {
                      transform: 'translateY(-4px)',
                      boxShadow: 4,
                    },
                  }}
                >
                  <CardMedia
                    component="div"
                    sx={{
                      height: { xs: 150, sm: 180, md: 200 },
                      backgroundColor: 'grey.200',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                    }}
                  >
                    {product.imageUrl ? (
                      <img
                        src={product.imageUrl}
                        alt={product.name}
                        style={{
                          width: '100%',
                          height: '100%',
                          objectFit: 'cover',
                        }}
                      />
                    ) : (
                      <Typography variant="body2" color="text.secondary">
                        No Image
                      </Typography>
                    )}
                  </CardMedia>
                  <CardContent sx={{ flexGrow: 1, p: { xs: 1, sm: 2 } }}>
                    <Typography
                      gutterBottom
                      variant="h6"
                      component="h2"
                      noWrap
                      title={product.name}
                      sx={{ fontSize: { xs: '0.9rem', sm: '1rem', md: '1.1rem' } }}
                    >
                      {product.name}
                    </Typography>
                    <Typography
                      variant="body2"
                      color="text.secondary"
                      sx={{
                        display: '-webkit-box',
                        WebkitLineClamp: 2,
                        WebkitBoxOrient: 'vertical',
                        overflow: 'hidden',
                        fontSize: { xs: '0.8rem', sm: '0.875rem' },
                        lineHeight: 1.3,
                        mb: 1,
                      }}
                    >
                      {product.description}
                    </Typography>
                    <Typography
                      variant="h6"
                      color="primary"
                      sx={{ 
                        mt: 1, 
                        fontWeight: 'bold',
                        fontSize: { xs: '1rem', sm: '1.1rem', md: '1.25rem' },
                      }}
                    >
                      ${product.price?.toFixed(2)}
                    </Typography>
                    {product.stock < 10 && product.stock > 0 && (
                      <Typography 
                        variant="body2" 
                        color="warning.main"
                        sx={{ fontSize: { xs: '0.75rem', sm: '0.875rem' } }}
                      >
                        Only {product.stock} left in stock
                      </Typography>
                    )}
                    {product.stock === 0 && (
                      <Typography 
                        variant="body2" 
                        color="error"
                        sx={{ fontSize: { xs: '0.75rem', sm: '0.875rem' } }}
                      >
                        Out of stock
                      </Typography>
                    )}
                  </CardContent>
                  <CardActions sx={{ p: { xs: 1, sm: 2 }, pt: 0 }}>
                    <Button
                      component={Link}
                      to={`/products/${product.id}`}
                      size="small"
                      sx={{ 
                        mr: 1,
                        fontSize: { xs: '0.7rem', sm: '0.875rem' },
                        minWidth: 'auto',
                      }}
                    >
                      Details
                    </Button>
                    <Button
                      size="small"
                      variant="contained"
                      startIcon={<AddShoppingCart sx={{ fontSize: { xs: 16, sm: 20 } }} />}
                      onClick={() => handleAddToCart(product)}
                      disabled={product.stock === 0}
                      sx={{ 
                        fontSize: { xs: '0.7rem', sm: '0.875rem' },
                        px: { xs: 1, sm: 2 },
                      }}
                    >
                      {window.innerWidth < 600 ? 'Add' : 'Add to Cart'}
                    </Button>
                  </CardActions>
                </Card>
              </Grid>
            ))}
          </Grid>

          {/* Pagination */}
          {productsData?.totalPages > 1 && (
            <Box display="flex" justifyContent="center" mt={4}>
              <Pagination
                count={productsData.totalPages}
                page={page}
                onChange={handlePageChange}
                color="primary"
                size="large"
              />
            </Box>
          )}

          {productsData?.content?.length === 0 && (
            <Box textAlign="center" py={4}>
              <Typography variant="h6" color="text.secondary">
                No products found matching your criteria.
              </Typography>
            </Box>
          )}
        </>
      )}
    </Container>
  );
};

export default Products;
