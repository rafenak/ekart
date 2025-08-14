import React, { useState } from 'react';
import {
  Container,
  Typography,
  Box,
  Grid,
  Button,
  Card,
  CardMedia,
  Rating,
  Chip,
  Divider,
  IconButton,
  Alert,
  CircularProgress,
} from '@mui/material';
import { Add, Remove, ShoppingCart, FavoriteOutlined } from '@mui/icons-material';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { useCart } from '../contexts/CartContext';
import toast from 'react-hot-toast';

const ProductDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { addToCart } = useCart();
  const [quantity, setQuantity] = useState(1);
  const [selectedImage, setSelectedImage] = useState(0);

  // Mock product data - replace with actual API call
  const {
    data: product,
    isLoading,
    error,
  } = useQuery({
    queryKey: ['product', id],
    queryFn: async () => {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      return {
        id: parseInt(id),
        name: 'Premium Wireless Headphones',
        description: 'High-quality wireless headphones with noise cancellation and premium sound quality. Perfect for music lovers and professionals.',
        price: 299.99,
        originalPrice: 399.99,
        rating: 4.5,
        reviewCount: 128,
        stock: 15,
        category: 'Electronics',
        brand: 'TechPro',
        images: [
          'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500',
          'https://images.unsplash.com/photo-1484704849700-f032a568e944?w=500',
          'https://images.unsplash.com/photo-1496957961599-e35b69ef5d7c?w=500',
        ],
        features: [
          'Active Noise Cancellation',
          '30-hour battery life',
          'Bluetooth 5.0',
          'Quick charge (5 min = 3 hours)',
          'Premium materials',
        ],
        specifications: {
          'Driver Size': '40mm',
          'Frequency Response': '20Hz - 20kHz',
          'Impedance': '32 ohms',
          'Weight': '250g',
          'Connectivity': 'Bluetooth 5.0, 3.5mm jack',
        },
      };
    },
  });

  const handleQuantityChange = (change) => {
    const newQuantity = quantity + change;
    if (newQuantity >= 1 && newQuantity <= product?.stock) {
      setQuantity(newQuantity);
    }
  };

  const handleAddToCart = () => {
    if (product) {
      addToCart({ ...product, quantity });
      toast.success(`Added ${quantity} ${product.name} to cart`);
    }
  };

  const handleBuyNow = () => {
    handleAddToCart();
    navigate('/cart');
  };

  if (isLoading) {
    return (
      <Container maxWidth="lg" sx={{ py: { xs: 2, sm: 4 } }}>
        <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
          <CircularProgress size={60} />
        </Box>
      </Container>
    );
  }

  if (error || !product) {
    return (
      <Container maxWidth="lg" sx={{ py: { xs: 2, sm: 4 } }}>
        <Alert severity="error">
          Product not found or failed to load.
        </Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: { xs: 2, sm: 4 } }}>
      <Grid container spacing={{ xs: 2, md: 4 }}>
        {/* Product Images */}
        <Grid item xs={12} md={6}>
          <Card sx={{ mb: 2 }}>
            <CardMedia
              component="img"
              sx={{
                height: { xs: 300, sm: 400, md: 500 },
                objectFit: 'cover',
              }}
              image={product.images[selectedImage]}
              alt={product.name}
            />
          </Card>
          
          {/* Thumbnail Images */}
          <Box sx={{ display: 'flex', gap: 1, justifyContent: 'center' }}>
            {product.images.map((image, index) => (
              <Box
                key={index}
                component="img"
                src={image}
                alt={`${product.name} ${index + 1}`}
                onClick={() => setSelectedImage(index)}
                sx={{
                  width: { xs: 60, sm: 80 },
                  height: { xs: 60, sm: 80 },
                  objectFit: 'cover',
                  borderRadius: 1,
                  cursor: 'pointer',
                  border: selectedImage === index ? '2px solid primary.main' : '2px solid transparent',
                  opacity: selectedImage === index ? 1 : 0.7,
                  '&:hover': { opacity: 1 },
                }}
              />
            ))}
          </Box>
        </Grid>

        {/* Product Information */}
        <Grid item xs={12} md={6}>
          <Box sx={{ pl: { md: 2 } }}>
            {/* Category & Brand */}
            <Box sx={{ mb: 2 }}>
              <Chip label={product.category} size="small" sx={{ mr: 1 }} />
              <Chip label={product.brand} variant="outlined" size="small" />
            </Box>

            {/* Product Name */}
            <Typography 
              variant="h4" 
              component="h1" 
              gutterBottom
              sx={{ fontSize: { xs: '1.5rem', sm: '2rem', md: '2.125rem' } }}
            >
              {product.name}
            </Typography>

            {/* Rating */}
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <Rating value={product.rating} precision={0.5} readOnly />
              <Typography 
                variant="body2" 
                color="text.secondary" 
                sx={{ ml: 1, fontSize: { xs: '0.8rem', sm: '0.875rem' } }}
              >
                ({product.reviewCount} reviews)
              </Typography>
            </Box>

            {/* Price */}
            <Box sx={{ mb: 3 }}>
              <Typography 
                variant="h4" 
                color="primary" 
                component="span"
                sx={{ 
                  fontWeight: 'bold',
                  fontSize: { xs: '1.5rem', sm: '2rem' },
                }}
              >
                ${product.price}
              </Typography>
              {product.originalPrice && (
                <Typography 
                  variant="h6" 
                  color="text.secondary" 
                  component="span"
                  sx={{ 
                    ml: 2, 
                    textDecoration: 'line-through',
                    fontSize: { xs: '1rem', sm: '1.25rem' },
                  }}
                >
                  ${product.originalPrice}
                </Typography>
              )}
            </Box>

            {/* Stock Status */}
            <Box sx={{ mb: 3 }}>
              {product.stock > 0 ? (
                <Typography 
                  color="success.main" 
                  variant="body1"
                  sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}
                >
                  ✓ In Stock ({product.stock} available)
                </Typography>
              ) : (
                <Typography 
                  color="error.main" 
                  variant="body1"
                  sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}
                >
                  Out of Stock
                </Typography>
              )}
            </Box>

            {/* Description */}
            <Typography 
              variant="body1" 
              paragraph
              sx={{ 
                mb: 3,
                fontSize: { xs: '0.9rem', sm: '1rem' },
                lineHeight: 1.6,
              }}
            >
              {product.description}
            </Typography>

            {/* Quantity Selector */}
            <Box sx={{ mb: 3 }}>
              <Typography 
                variant="subtitle1" 
                gutterBottom
                sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}
              >
                Quantity:
              </Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <IconButton
                    onClick={() => handleQuantityChange(-1)}
                    disabled={quantity <= 1}
                    size={window.innerWidth < 600 ? 'small' : 'medium'}
                  >
                    <Remove />
                  </IconButton>
                  <Typography 
                    sx={{ 
                      mx: 2, 
                      minWidth: '40px', 
                      textAlign: 'center',
                      fontSize: { xs: '1rem', sm: '1.1rem' },
                    }}
                  >
                    {quantity}
                  </Typography>
                  <IconButton
                    onClick={() => handleQuantityChange(1)}
                    disabled={quantity >= product.stock}
                    size={window.innerWidth < 600 ? 'small' : 'medium'}
                  >
                    <Add />
                  </IconButton>
                </Box>
              </Box>
            </Box>

            {/* Action Buttons */}
            <Box sx={{ display: 'flex', gap: 2, mb: 3, flexDirection: { xs: 'column', sm: 'row' } }}>
              <Button
                variant="contained"
                size="large"
                startIcon={<ShoppingCart />}
                onClick={handleAddToCart}
                disabled={product.stock === 0}
                sx={{ 
                  flex: 1,
                  py: { xs: 1.5, sm: 2 },
                  fontSize: { xs: '0.9rem', sm: '1rem' },
                }}
              >
                Add to Cart
              </Button>
              <Button
                variant="outlined"
                size="large"
                onClick={handleBuyNow}
                disabled={product.stock === 0}
                sx={{ 
                  flex: 1,
                  py: { xs: 1.5, sm: 2 },
                  fontSize: { xs: '0.9rem', sm: '1rem' },
                }}
              >
                Buy Now
              </Button>
              <IconButton
                size="large"
                sx={{ 
                  border: '1px solid',
                  borderColor: 'divider',
                  '&:hover': { borderColor: 'primary.main' },
                }}
              >
                <FavoriteOutlined />
              </IconButton>
            </Box>

            {/* Features */}
            <Box sx={{ mb: 3 }}>
              <Typography 
                variant="subtitle1" 
                gutterBottom
                sx={{ fontSize: { xs: '1rem', sm: '1.1rem' } }}
              >
                Key Features:
              </Typography>
              {product.features.map((feature, index) => (
                <Typography 
                  key={index} 
                  variant="body2" 
                  sx={{ 
                    mb: 0.5,
                    fontSize: { xs: '0.8rem', sm: '0.875rem' },
                  }}
                >
                  • {feature}
                </Typography>
              ))}
            </Box>

            <Divider sx={{ my: 3 }} />

            {/* Specifications */}
            <Box>
              <Typography 
                variant="subtitle1" 
                gutterBottom
                sx={{ fontSize: { xs: '1rem', sm: '1.1rem' } }}
              >
                Specifications:
              </Typography>
              {Object.entries(product.specifications).map(([key, value]) => (
                <Box 
                  key={key} 
                  sx={{ 
                    display: 'flex', 
                    justifyContent: 'space-between', 
                    mb: 1,
                    flexDirection: { xs: 'column', sm: 'row' },
                  }}
                >
                  <Typography 
                    variant="body2" 
                    color="text.secondary"
                    sx={{ fontSize: { xs: '0.8rem', sm: '0.875rem' } }}
                  >
                    {key}:
                  </Typography>
                  <Typography 
                    variant="body2"
                    sx={{ fontSize: { xs: '0.8rem', sm: '0.875rem' } }}
                  >
                    {value}
                  </Typography>
                </Box>
              ))}
            </Box>
          </Box>
        </Grid>
      </Grid>
    </Container>
  );
};

export default ProductDetail;
