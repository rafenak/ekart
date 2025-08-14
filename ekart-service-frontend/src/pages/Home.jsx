import React from 'react';
import {
  Container,
  Typography,
  Box,
  Button,
  Grid,
  Card,
  CardContent,
  CardActions,
  CardMedia,
} from '@mui/material';
import { Link } from 'react-router-dom';
import { ShoppingCart, LocalShipping, Security, Support } from '@mui/icons-material';

const Home = () => {
  const features = [
    {
      icon: <ShoppingCart sx={{ fontSize: 40, color: 'primary.main' }} />,
      title: 'Easy Shopping',
      description: 'Browse and shop from thousands of products with just a few clicks.',
    },
    {
      icon: <LocalShipping sx={{ fontSize: 40, color: 'primary.main' }} />,
      title: 'Fast Delivery',
      description: 'Get your orders delivered quickly with our efficient delivery network.',
    },
    {
      icon: <Security sx={{ fontSize: 40, color: 'primary.main' }} />,
      title: 'Secure Payments',
      description: 'Shop with confidence using our secure payment gateway.',
    },
    {
      icon: <Support sx={{ fontSize: 40, color: 'primary.main' }} />,
      title: '24/7 Support',
      description: 'Our customer support team is always ready to help you.',
    },
  ];

  return (
    <Container maxWidth="lg">
      {/* Hero Section */}
      <Box
        sx={{
          display: 'flex',
          flexDirection: { xs: 'column', md: 'row' },
          alignItems: 'center',
          py: { xs: 4, md: 8 },
          gap: { xs: 3, md: 4 },
          minHeight: { xs: 'auto', md: '60vh' },
        }}
      >
        <Box sx={{ flex: 1, textAlign: { xs: 'center', md: 'left' } }}>
          <Typography
            variant="h2"
            component="h1"
            gutterBottom
            sx={{
              fontWeight: 'bold',
              color: 'primary.main',
              fontSize: { xs: '2rem', sm: '2.5rem', md: '3.5rem' },
              lineHeight: { xs: 1.2, md: 1.1 },
            }}
          >
            Welcome to E-Kart
          </Typography>
          <Typography
            variant="h5"
            color="text.secondary"
            paragraph
            sx={{ 
              mb: 4,
              fontSize: { xs: '1.1rem', sm: '1.25rem', md: '1.5rem' },
              lineHeight: 1.4,
              maxWidth: { xs: '100%', md: '90%' },
            }}
          >
            Your one-stop destination for all your shopping needs. 
            Discover amazing products at unbeatable prices with fast delivery.
          </Typography>
          <Box sx={{ 
            display: 'flex', 
            gap: 2, 
            flexDirection: { xs: 'column', sm: 'row' },
            justifyContent: { xs: 'center', md: 'flex-start' },
            alignItems: 'center',
          }}>
            <Button
              component={Link}
              to="/products"
              variant="contained"
              size="large"
              sx={{ 
                px: { xs: 3, md: 4 }, 
                py: { xs: 1.2, md: 1.5 },
                fontSize: { xs: '1rem', md: '1.1rem' },
                minWidth: { xs: 200, sm: 'auto' },
              }}
            >
              Shop Now
            </Button>
            <Button
              component={Link}
              to="/products"
              variant="outlined"
              size="large"
              sx={{ 
                px: { xs: 3, md: 4 }, 
                py: { xs: 1.2, md: 1.5 },
                fontSize: { xs: '1rem', md: '1.1rem' },
                minWidth: { xs: 200, sm: 'auto' },
              }}
            >
              Browse Categories
            </Button>
          </Box>
        </Box>
        <Box
          sx={{
            flex: 1,
            display: 'flex',
            justifyContent: 'center',
            width: '100%',
          }}
        >
          <Box
            sx={{
              width: { xs: 280, sm: 350, md: 400 },
              height: { xs: 200, sm: 250, md: 300 },
              backgroundColor: 'grey.200',
              borderRadius: 2,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              boxShadow: 2,
            }}
          >
            <Typography variant="h6" color="text.secondary">
              Hero Image Placeholder
            </Typography>
          </Box>
        </Box>
      </Box>

      {/* Features Section */}
      <Box sx={{ py: { xs: 4, md: 8 } }}>
        <Typography
          variant="h3"
          component="h2"
          align="center"
          gutterBottom
          sx={{ 
            mb: { xs: 4, md: 6 }, 
            fontWeight: 'bold',
            fontSize: { xs: '2rem', sm: '2.5rem', md: '3rem' },
          }}
        >
          Why Choose E-Kart?
        </Typography>
        <Grid container spacing={{ xs: 2, md: 4 }}>
          {features.map((feature, index) => (
            <Grid item xs={12} sm={6} md={3} key={index}>
              <Card
                sx={{
                  height: '100%',
                  display: 'flex',
                  flexDirection: 'column',
                  alignItems: 'center',
                  textAlign: 'center',
                  p: { xs: 2, md: 3 },
                  transition: 'all 0.3s ease-in-out',
                  '&:hover': {
                    transform: 'translateY(-4px)',
                    boxShadow: 4,
                  },
                }}
              >
                <Box sx={{ mb: 2 }}>
                  {feature.icon}
                </Box>
                <CardContent sx={{ flexGrow: 1, p: 0 }}>
                  <Typography 
                    gutterBottom 
                    variant="h6" 
                    component="h3"
                    sx={{ fontSize: { xs: '1.1rem', md: '1.25rem' } }}
                  >
                    {feature.title}
                  </Typography>
                  <Typography 
                    variant="body2" 
                    color="text.secondary"
                    sx={{ fontSize: { xs: '0.9rem', md: '1rem' } }}
                  >
                    {feature.description}
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Box>

      {/* Call to Action Section */}
      <Box
        sx={{
          py: { xs: 4, md: 6 },
          px: { xs: 2, md: 4 },
          backgroundColor: 'primary.main',
          color: 'white',
          borderRadius: 2,
          textAlign: 'center',
          mb: 4,
          mx: { xs: 1, sm: 0 },
        }}
      >
        <Typography 
          variant="h4" 
          component="h2" 
          gutterBottom
          sx={{ fontSize: { xs: '1.5rem', sm: '2rem', md: '2.5rem' } }}
        >
          Ready to Start Shopping?
        </Typography>
        <Typography 
          variant="h6" 
          paragraph 
          sx={{ 
            mb: { xs: 3, md: 4 },
            fontSize: { xs: '1rem', sm: '1.1rem', md: '1.25rem' },
            opacity: 0.9,
          }}
        >
          Join thousands of satisfied customers and discover amazing deals today!
        </Typography>
        <Button
          component={Link}
          to="/products"
          variant="contained"
          color="secondary"
          size="large"
          sx={{ 
            px: { xs: 4, md: 6 }, 
            py: { xs: 1.2, md: 1.5 },
            fontSize: { xs: '1rem', md: '1.1rem' },
            fontWeight: 'bold',
          }}
        >
          Explore Products
        </Button>
      </Box>
    </Container>
  );
};

export default Home;
