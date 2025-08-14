import React, { useState } from 'react';
import {
  Container,
  Typography,
  Box,
  Grid,
  Card,
  CardContent,
  TextField,
  Button,
  Stepper,
  Step,
  StepLabel,
  RadioGroup,
  FormControlLabel,
  Radio,
  FormControl,
  FormLabel,
  Divider,
  List,
  ListItem,
  ListItemText,
  Alert,
  Checkbox,
} from '@mui/material';
import {
  CheckCircle,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../contexts/CartContext';
import { useAuth } from '../hooks/useAuth';
import toast from 'react-hot-toast';

const steps = ['Shipping Address', 'Payment Method', 'Review Order'];

const Checkout = () => {
  const navigate = useNavigate();
  const { cart, getTotalPrice, clearCart } = useCart();
  const { user } = useAuth();
  const [activeStep, setActiveStep] = useState(0);
  const [shippingInfo, setShippingInfo] = useState({
    firstName: user?.firstName || '',
    lastName: user?.lastName || '',
    email: user?.email || '',
    phone: user?.phone || '',
    address: '',
    city: '',
    state: '',
    zipCode: '',
    country: 'United States',
  });
  const [paymentMethod, setPaymentMethod] = useState('card');
  const [cardInfo, setCardInfo] = useState({
    cardNumber: '',
    expiryDate: '',
    cvv: '',
    nameOnCard: '',
  });
  const [orderPlaced, setOrderPlaced] = useState(false);

  const subtotal = getTotalPrice();
  const shipping = subtotal > 50 ? 0 : 9.99;
  const tax = subtotal * 0.1;
  const total = subtotal + shipping + tax;

  const handleNext = () => {
    if (activeStep === 0) {
      // Validate shipping information
      const requiredFields = ['firstName', 'lastName', 'email', 'phone', 'address', 'city', 'state', 'zipCode'];
      const missingFields = requiredFields.filter(field => !shippingInfo[field]);
      
      if (missingFields.length > 0) {
        toast.error('Please fill in all required shipping information');
        return;
      }
    }
    
    if (activeStep === 1) {
      // Validate payment information
      if (paymentMethod === 'card') {
        const requiredFields = ['cardNumber', 'expiryDate', 'cvv', 'nameOnCard'];
        const missingFields = requiredFields.filter(field => !cardInfo[field]);
        
        if (missingFields.length > 0) {
          toast.error('Please fill in all required payment information');
          return;
        }
      }
    }

    setActiveStep((prevActiveStep) => prevActiveStep + 1);
  };

  const handleBack = () => {
    setActiveStep((prevActiveStep) => prevActiveStep - 1);
  };

  const handlePlaceOrder = async () => {
    try {
      // Simulate order placement
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      setOrderPlaced(true);
      clearCart();
      toast.success('Order placed successfully!');
      
      // Redirect to order confirmation after 3 seconds
      setTimeout(() => {
        navigate('/profile');
      }, 3000);
    } catch {
      toast.error('Failed to place order. Please try again.');
    }
  };

  const handleShippingChange = (field) => (event) => {
    setShippingInfo({
      ...shippingInfo,
      [field]: event.target.value,
    });
  };

  const handleCardChange = (field) => (event) => {
    setCardInfo({
      ...cardInfo,
      [field]: event.target.value,
    });
  };

  if (cart.length === 0 && !orderPlaced) {
    return (
      <Container maxWidth="lg" sx={{ py: { xs: 2, sm: 4 } }}>
        <Alert severity="warning">
          Your cart is empty. Please add items to proceed with checkout.
        </Alert>
      </Container>
    );
  }

  if (orderPlaced) {
    return (
      <Container maxWidth="sm" sx={{ py: { xs: 4, sm: 8 }, textAlign: 'center' }}>
        <CheckCircle sx={{ fontSize: { xs: 60, sm: 80 }, color: 'success.main', mb: 2 }} />
        <Typography 
          variant="h4" 
          gutterBottom
          sx={{ fontSize: { xs: '1.5rem', sm: '2rem' } }}
        >
          Order Confirmed!
        </Typography>
        <Typography 
          variant="body1" 
          color="text.secondary" 
          paragraph
          sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}
        >
          Thank you for your order. You will receive a confirmation email shortly.
        </Typography>
        <Typography 
          variant="body2" 
          color="text.secondary"
          sx={{ fontSize: { xs: '0.8rem', sm: '0.875rem' } }}
        >
          Redirecting to your profile...
        </Typography>
      </Container>
    );
  }

  const renderStepContent = (step) => {
    switch (step) {
      case 0:
        return (
          <Grid container spacing={{ xs: 2, sm: 3 }}>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                label="First Name"
                value={shippingInfo.firstName}
                onChange={handleShippingChange('firstName')}
                sx={{
                  '& .MuiInputBase-input': {
                    fontSize: { xs: '0.9rem', sm: '1rem' },
                  },
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                label="Last Name"
                value={shippingInfo.lastName}
                onChange={handleShippingChange('lastName')}
                sx={{
                  '& .MuiInputBase-input': {
                    fontSize: { xs: '0.9rem', sm: '1rem' },
                  },
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                label="Email"
                type="email"
                value={shippingInfo.email}
                onChange={handleShippingChange('email')}
                sx={{
                  '& .MuiInputBase-input': {
                    fontSize: { xs: '0.9rem', sm: '1rem' },
                  },
                }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                label="Phone"
                value={shippingInfo.phone}
                onChange={handleShippingChange('phone')}
                sx={{
                  '& .MuiInputBase-input': {
                    fontSize: { xs: '0.9rem', sm: '1rem' },
                  },
                }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                label="Address"
                value={shippingInfo.address}
                onChange={handleShippingChange('address')}
                sx={{
                  '& .MuiInputBase-input': {
                    fontSize: { xs: '0.9rem', sm: '1rem' },
                  },
                }}
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <TextField
                required
                fullWidth
                label="City"
                value={shippingInfo.city}
                onChange={handleShippingChange('city')}
                sx={{
                  '& .MuiInputBase-input': {
                    fontSize: { xs: '0.9rem', sm: '1rem' },
                  },
                }}
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <TextField
                required
                fullWidth
                label="State"
                value={shippingInfo.state}
                onChange={handleShippingChange('state')}
                sx={{
                  '& .MuiInputBase-input': {
                    fontSize: { xs: '0.9rem', sm: '1rem' },
                  },
                }}
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <TextField
                required
                fullWidth
                label="ZIP Code"
                value={shippingInfo.zipCode}
                onChange={handleShippingChange('zipCode')}
                sx={{
                  '& .MuiInputBase-input': {
                    fontSize: { xs: '0.9rem', sm: '1rem' },
                  },
                }}
              />
            </Grid>
          </Grid>
        );

      case 1:
        return (
          <Box>
            <FormControl component="fieldset" sx={{ mb: 3 }}>
              <FormLabel component="legend">Payment Method</FormLabel>
              <RadioGroup
                value={paymentMethod}
                onChange={(e) => setPaymentMethod(e.target.value)}
              >
                <FormControlLabel
                  value="card"
                  control={<Radio />}
                  label="Credit/Debit Card"
                />
                <FormControlLabel
                  value="paypal"
                  control={<Radio />}
                  label="PayPal"
                />
                <FormControlLabel
                  value="cod"
                  control={<Radio />}
                  label="Cash on Delivery"
                />
              </RadioGroup>
            </FormControl>

            {paymentMethod === 'card' && (
              <Grid container spacing={{ xs: 2, sm: 3 }}>
                <Grid item xs={12}>
                  <TextField
                    required
                    fullWidth
                    label="Name on Card"
                    value={cardInfo.nameOnCard}
                    onChange={handleCardChange('nameOnCard')}
                    sx={{
                      '& .MuiInputBase-input': {
                        fontSize: { xs: '0.9rem', sm: '1rem' },
                      },
                    }}
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    required
                    fullWidth
                    label="Card Number"
                    placeholder="1234 5678 9012 3456"
                    value={cardInfo.cardNumber}
                    onChange={handleCardChange('cardNumber')}
                    sx={{
                      '& .MuiInputBase-input': {
                        fontSize: { xs: '0.9rem', sm: '1rem' },
                      },
                    }}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    required
                    fullWidth
                    label="Expiry Date"
                    placeholder="MM/YY"
                    value={cardInfo.expiryDate}
                    onChange={handleCardChange('expiryDate')}
                    sx={{
                      '& .MuiInputBase-input': {
                        fontSize: { xs: '0.9rem', sm: '1rem' },
                      },
                    }}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    required
                    fullWidth
                    label="CVV"
                    placeholder="123"
                    value={cardInfo.cvv}
                    onChange={handleCardChange('cvv')}
                    sx={{
                      '& .MuiInputBase-input': {
                        fontSize: { xs: '0.9rem', sm: '1rem' },
                      },
                    }}
                  />
                </Grid>
              </Grid>
            )}

            {paymentMethod === 'paypal' && (
              <Alert severity="info" sx={{ mt: 2 }}>
                You will be redirected to PayPal to complete your payment.
              </Alert>
            )}

            {paymentMethod === 'cod' && (
              <Alert severity="info" sx={{ mt: 2 }}>
                You will pay cash when your order is delivered.
              </Alert>
            )}
          </Box>
        );

      case 2:
        return (
          <Grid container spacing={{ xs: 2, md: 3 }}>
            <Grid item xs={12} md={8}>
              <Typography 
                variant="h6" 
                gutterBottom
                sx={{ fontSize: { xs: '1.1rem', sm: '1.25rem' } }}
              >
                Order Items
              </Typography>
              <List>
                {cart.map((item) => (
                  <ListItem key={item.id} sx={{ py: 1 }}>
                    <ListItemText
                      primary={
                        <Typography sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}>
                          {item.name}
                        </Typography>
                      }
                      secondary={
                        <Typography 
                          variant="body2" 
                          color="text.secondary"
                          sx={{ fontSize: { xs: '0.8rem', sm: '0.875rem' } }}
                        >
                          Quantity: {item.quantity} × ${item.price}
                        </Typography>
                      }
                    />
                    <Typography 
                      variant="body1"
                      sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}
                    >
                      ${(item.price * item.quantity).toFixed(2)}
                    </Typography>
                  </ListItem>
                ))}
              </List>

              <Divider sx={{ my: 2 }} />

              <Typography 
                variant="h6" 
                gutterBottom
                sx={{ fontSize: { xs: '1.1rem', sm: '1.25rem' } }}
              >
                Shipping Address
              </Typography>
              <Typography 
                variant="body2"
                sx={{ fontSize: { xs: '0.8rem', sm: '0.875rem' } }}
              >
                {shippingInfo.firstName} {shippingInfo.lastName}<br />
                {shippingInfo.address}<br />
                {shippingInfo.city}, {shippingInfo.state} {shippingInfo.zipCode}<br />
                {shippingInfo.phone}
              </Typography>

              <Divider sx={{ my: 2 }} />

              <Typography 
                variant="h6" 
                gutterBottom
                sx={{ fontSize: { xs: '1.1rem', sm: '1.25rem' } }}
              >
                Payment Method
              </Typography>
              <Typography 
                variant="body2"
                sx={{ fontSize: { xs: '0.8rem', sm: '0.875rem' } }}
              >
                {paymentMethod === 'card' && `Card ending in ${cardInfo.cardNumber.slice(-4)}`}
                {paymentMethod === 'paypal' && 'PayPal'}
                {paymentMethod === 'cod' && 'Cash on Delivery'}
              </Typography>
            </Grid>

            <Grid item xs={12} md={4}>
              <Card>
                <CardContent sx={{ p: { xs: 2, sm: 3 } }}>
                  <Typography 
                    variant="h6" 
                    gutterBottom
                    sx={{ fontSize: { xs: '1.1rem', sm: '1.25rem' } }}
                  >
                    Order Summary
                  </Typography>
                  
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}>
                      Subtotal:
                    </Typography>
                    <Typography sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}>
                      ${subtotal.toFixed(2)}
                    </Typography>
                  </Box>
                  
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}>
                      Shipping:
                    </Typography>
                    <Typography sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}>
                      ${shipping.toFixed(2)}
                    </Typography>
                  </Box>
                  
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}>
                      Tax:
                    </Typography>
                    <Typography sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}>
                      ${tax.toFixed(2)}
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
                      ${total.toFixed(2)}
                    </Typography>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        );

      default:
        return 'Unknown step';
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: { xs: 2, sm: 4 } }}>
      <Typography 
        variant="h4" 
        component="h1" 
        gutterBottom
        sx={{ 
          fontSize: { xs: '1.75rem', sm: '2rem', md: '2.125rem' },
          mb: { xs: 2, sm: 4 },
        }}
      >
        Checkout
      </Typography>

      <Stepper 
        activeStep={activeStep} 
        sx={{ 
          mb: 4,
          '& .MuiStepLabel-label': {
            fontSize: { xs: '0.8rem', sm: '0.875rem' },
          },
        }}
      >
        {steps.map((label) => (
          <Step key={label}>
            <StepLabel>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>

      <Card sx={{ mb: 3 }}>
        <CardContent sx={{ p: { xs: 2, sm: 4 } }}>
          {renderStepContent(activeStep)}
        </CardContent>
      </Card>

      <Box sx={{ display: 'flex', justifyContent: 'space-between', flexDirection: { xs: 'column', sm: 'row' }, gap: 2 }}>
        <Button
          disabled={activeStep === 0}
          onClick={handleBack}
          sx={{ 
            fontSize: { xs: '0.9rem', sm: '1rem' },
            order: { xs: 2, sm: 1 },
          }}
        >
          Back
        </Button>
        <Button
          variant="contained"
          onClick={activeStep === steps.length - 1 ? handlePlaceOrder : handleNext}
          sx={{ 
            fontSize: { xs: '0.9rem', sm: '1rem' },
            order: { xs: 1, sm: 2 },
          }}
        >
          {activeStep === steps.length - 1 ? 'Place Order' : 'Next'}
        </Button>
      </Box>
    </Container>
  );
};

export default Checkout;
