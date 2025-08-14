import React, { useContext } from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  Grid,
  Button,
  IconButton,
  TextField,
  Divider,
  Paper,
} from '@mui/material';
import { Add, Remove, Delete } from '@mui/icons-material';
import { Link, useNavigate } from 'react-router-dom';
import { CartContext } from '../contexts/CartContext';
import { useAuth } from '../hooks/useAuth';

const Cart = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const {
    cartItems,
    totalAmount,
    updateQuantity,
    removeFromCart,
    clearCart,
  } = useContext(CartContext);

  const handleQuantityChange = (productId, newQuantity) => {
    if (newQuantity < 1) return;
    updateQuantity(productId, newQuantity);
  };

  const handleCheckout = () => {
    if (!isAuthenticated) {
      navigate('/login', { state: { from: { pathname: '/checkout' } } });
    } else {
      navigate('/checkout');
    }
  };

  if (cartItems.length === 0) {
    return (
      <Container maxWidth="lg" sx={{ py: { xs: 2, sm: 4 } }}>
        <Paper sx={{ p: { xs: 3, sm: 4 }, textAlign: 'center' }}>
          <Typography 
            variant="h4" 
            gutterBottom
            sx={{ fontSize: { xs: '1.75rem', sm: '2rem', md: '2.125rem' } }}
          >
            Your Cart is Empty
          </Typography>
          <Typography 
            variant="body1" 
            color="text.secondary" 
            paragraph
            sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}
          >
            Looks like you haven't added any items to your cart yet.
          </Typography>
          <Button
            component={Link}
            to="/products"
            variant="contained"
            size="large"
            sx={{
              px: { xs: 3, sm: 4 },
              py: { xs: 1, sm: 1.5 },
              fontSize: { xs: '0.9rem', sm: '1rem' },
            }}
          >
            Start Shopping
          </Button>
        </Paper>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: { xs: 2, sm: 4 } }}>
      <Typography 
        variant="h4" 
        component="h1" 
        gutterBottom
        sx={{ 
          fontSize: { xs: '1.75rem', sm: '2rem', md: '2.125rem' },
          mb: { xs: 2, sm: 3 },
        }}
      >
        Shopping Cart
      </Typography>

      <Grid container spacing={{ xs: 2, md: 3 }}>
        {/* Cart Items */}
        <Grid item xs={12} md={8}>
          <Box sx={{ 
            mb: 2, 
            display: 'flex', 
            justifyContent: 'space-between', 
            alignItems: 'center',
            flexDirection: { xs: 'column', sm: 'row' },
            gap: { xs: 1, sm: 0 },
          }}>
            <Typography 
              variant="h6"
              sx={{ fontSize: { xs: '1rem', sm: '1.25rem' } }}
            >
              {cartItems.length} {cartItems.length === 1 ? 'Item' : 'Items'}
            </Typography>
            <Button
              color="error"
              onClick={clearCart}
              startIcon={<Delete />}
              size={window.innerWidth < 600 ? 'small' : 'medium'}
              sx={{ fontSize: { xs: '0.8rem', sm: '0.875rem' } }}
            >
              Clear Cart
            </Button>
          </Box>

          {cartItems.map((item) => (
            <Card key={item.id} sx={{ mb: 2 }}>
              <CardContent sx={{ p: { xs: 2, sm: 3 } }}>
                <Grid container spacing={{ xs: 2, sm: 2 }} alignItems="center">
                  {/* Product Image */}
                  <Grid item xs={4} sm={3}>
                    <Box
                      sx={{
                        width: '100%',
                        height: { xs: 80, sm: 100, md: 120 },
                        backgroundColor: 'grey.200',
                        borderRadius: 1,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                      }}
                    >
                      {item.imageUrl ? (
                        <img
                          src={item.imageUrl}
                          alt={item.name}
                          style={{
                            width: '100%',
                            height: '100%',
                            objectFit: 'cover',
                            borderRadius: 4,
                          }}
                        />
                      ) : (
                        <Typography 
                          variant="body2" 
                          color="text.secondary"
                          sx={{ fontSize: { xs: '0.7rem', sm: '0.875rem' } }}
                        >
                          No Image
                        </Typography>
                      )}
                    </Box>
                  </Grid>

                  {/* Product Details */}
                  <Grid item xs={8} sm={5}>
                    <Typography 
                      variant="h6" 
                      gutterBottom
                      sx={{ 
                        fontSize: { xs: '1rem', sm: '1.1rem', md: '1.25rem' },
                        lineHeight: 1.2,
                      }}
                    >
                      {item.name}
                    </Typography>
                    <Typography 
                      variant="body2" 
                      color="text.secondary" 
                      paragraph
                      sx={{ 
                        fontSize: { xs: '0.8rem', sm: '0.875rem' },
                        display: { xs: 'none', sm: 'block' },
                        mb: { xs: 1, sm: 2 },
                      }}
                    >
                      {item.description}
                    </Typography>
                    <Typography 
                      variant="h6" 
                      color="primary"
                      sx={{ 
                        fontSize: { xs: '1rem', sm: '1.1rem', md: '1.25rem' },
                        fontWeight: 'bold',
                      }}
                    >
                      ${item.price?.toFixed(2)}
                    </Typography>
                  </Grid>

                  {/* Quantity Controls */}
                  <Grid item xs={12} sm={4}>
                    <Box sx={{ 
                      display: 'flex', 
                      alignItems: 'center', 
                      gap: 1, 
                      mb: { xs: 1, sm: 2 },
                      justifyContent: { xs: 'space-between', sm: 'flex-start' },
                    }}>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <IconButton
                          onClick={() => handleQuantityChange(item.id, item.quantity - 1)}
                          disabled={item.quantity <= 1}
                          size={window.innerWidth < 600 ? 'small' : 'medium'}
                        >
                          <Remove />
                        </IconButton>
                        <TextField
                          size="small"
                          value={item.quantity}
                          onChange={(e) => {
                            const value = parseInt(e.target.value) || 1;
                            handleQuantityChange(item.id, value);
                          }}
                          sx={{ width: { xs: 50, sm: 60 } }}
                          inputProps={{
                            min: 1,
                            style: { 
                              textAlign: 'center',
                              fontSize: window.innerWidth < 600 ? '0.8rem' : '1rem',
                            },
                          }}
                        />
                        <IconButton
                          onClick={() => handleQuantityChange(item.id, item.quantity + 1)}
                          size={window.innerWidth < 600 ? 'small' : 'medium'}
                        >
                          <Add />
                        </IconButton>
                      </Box>
                      <IconButton
                        color="error"
                        onClick={() => removeFromCart(item.id)}
                        size={window.innerWidth < 600 ? 'small' : 'medium'}
                        sx={{ ml: { xs: 0, sm: 2 } }}
                      >
                        <Delete />
                      </IconButton>
                    </Box>
                    <Box sx={{ 
                      display: 'flex', 
                      justifyContent: { xs: 'center', sm: 'space-between' }, 
                      alignItems: 'center' 
                    }}>
                      <Typography 
                        variant="body1" 
                        fontWeight="bold"
                        sx={{ fontSize: { xs: '1rem', sm: '1.1rem' } }}
                      >
                        Total: ${(item.price * item.quantity).toFixed(2)}
                      </Typography>
                    </Box>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          ))}
        </Grid>

        {/* Order Summary */}
        <Grid item xs={12} md={4}>
          <Paper sx={{ 
            p: { xs: 2, sm: 3 }, 
            position: { md: 'sticky' }, 
            top: { md: 20 } 
          }}>
            <Typography 
              variant="h6" 
              gutterBottom
              sx={{ fontSize: { xs: '1.1rem', sm: '1.25rem' } }}
            >
              Order Summary
            </Typography>
            <Divider sx={{ my: 2 }} />
            
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
              <Typography sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}>
                Subtotal:
              </Typography>
              <Typography sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}>
                ${totalAmount.toFixed(2)}
              </Typography>
            </Box>
            
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
              <Typography sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}>
                Shipping:
              </Typography>
              <Typography sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}>
                Free
              </Typography>
            </Box>
            
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
              <Typography sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}>
                Tax:
              </Typography>
              <Typography sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}>
                ${(totalAmount * 0.1).toFixed(2)}
              </Typography>
            </Box>
            
            <Divider sx={{ my: 2 }} />
            
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
              <Typography 
                variant="h6"
                sx={{ fontSize: { xs: '1.1rem', sm: '1.25rem' } }}
              >
                Total:
              </Typography>
              <Typography 
                variant="h6" 
                color="primary"
                sx={{ 
                  fontSize: { xs: '1.1rem', sm: '1.25rem' },
                  fontWeight: 'bold',
                }}
              >
                ${(totalAmount * 1.1).toFixed(2)}
              </Typography>
            </Box>
            
            <Button
              fullWidth
              variant="contained"
              size="large"
              onClick={handleCheckout}
              sx={{ 
                mb: 2,
                py: { xs: 1.5, sm: 2 },
                fontSize: { xs: '0.9rem', sm: '1rem' },
                fontWeight: 'bold',
              }}
            >
              {isAuthenticated ? 'Proceed to Checkout' : 'Login to Checkout'}
            </Button>
            
            <Button
              fullWidth
              variant="outlined"
              component={Link}
              to="/products"
              sx={{ 
                py: { xs: 1, sm: 1.5 },
                fontSize: { xs: '0.9rem', sm: '1rem' },
              }}
            >
              Continue Shopping
            </Button>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default Cart;
