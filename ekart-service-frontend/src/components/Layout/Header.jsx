import React, { useState } from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
  Badge,
  IconButton,
  Menu,
  MenuItem,
  Container,
  Drawer,
  List,
  ListItem,
  ListItemText,
  useMediaQuery,
  useTheme,
} from '@mui/material';
import {
  ShoppingCart,
  AccountCircle,
  Menu as MenuIcon,
  Store,
} from '@mui/icons-material';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { useContext } from 'react';
import { CartContext } from '../../contexts/CartContext';

const Header = () => {
  const navigate = useNavigate();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const { isAuthenticated, user, logout } = useAuth();
  const { itemCount } = useContext(CartContext);
  
  const [anchorEl, setAnchorEl] = useState(null);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleProfileMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    logout();
    handleMenuClose();
    navigate('/');
  };

  const handleMobileMenuToggle = () => {
    setMobileMenuOpen(!mobileMenuOpen);
  };

  const menuItems = [
    { label: 'Home', path: '/' },
    { label: 'Products', path: '/products' },
  ];

  const mobileDrawer = (
    <Box sx={{ width: 250 }} role="presentation">
      <List>
        {menuItems.map((item) => (
          <ListItem
            button
            key={item.label}
            component={Link}
            to={item.path}
            onClick={() => setMobileMenuOpen(false)}
          >
            <ListItemText primary={item.label} />
          </ListItem>
        ))}
        {!isAuthenticated ? (
          <>
            <ListItem button component={Link} to="/login" onClick={() => setMobileMenuOpen(false)}>
              <ListItemText primary="Login" />
            </ListItem>
            <ListItem button component={Link} to="/register" onClick={() => setMobileMenuOpen(false)}>
              <ListItemText primary="Register" />
            </ListItem>
          </>
        ) : (
          <>
            <ListItem button component={Link} to="/profile" onClick={() => setMobileMenuOpen(false)}>
              <ListItemText primary="Profile" />
            </ListItem>
            <ListItem button component={Link} to="/orders" onClick={() => setMobileMenuOpen(false)}>
              <ListItemText primary="Orders" />
            </ListItem>
            <ListItem button onClick={() => { handleLogout(); setMobileMenuOpen(false); }}>
              <ListItemText primary="Logout" />
            </ListItem>
          </>
        )}
      </List>
    </Box>
  );

  return (
    <AppBar position="sticky">
      <Container maxWidth="lg">
        <Toolbar>
          {/* Mobile Menu Icon */}
          {isMobile && (
            <IconButton
              color="inherit"
              edge="start"
              onClick={handleMobileMenuToggle}
              sx={{ mr: 2 }}
            >
              <MenuIcon />
            </IconButton>
          )}

          {/* Logo */}
          <Box
            component={Link}
            to="/"
            sx={{
              display: 'flex',
              alignItems: 'center',
              textDecoration: 'none',
              color: 'inherit',
              flexGrow: isMobile ? 1 : 0,
              mr: { md: 4 },
            }}
          >
            <Store sx={{ mr: 1, fontSize: { xs: 24, md: 28 } }} />
            <Typography 
              variant="h6" 
              component="div"
              sx={{ 
                fontSize: { xs: '1.1rem', md: '1.25rem' },
                fontWeight: 'bold',
              }}
            >
              E-Kart
            </Typography>
          </Box>

          {/* Desktop Navigation */}
          {!isMobile && (
            <Box sx={{ flexGrow: 1, display: 'flex', alignItems: 'center' }}>
              {menuItems.map((item) => (
                <Button
                  key={item.label}
                  component={Link}
                  to={item.path}
                  color="inherit"
                  sx={{ 
                    mr: 2,
                    fontSize: '1rem',
                    textTransform: 'none',
                    '&:hover': {
                      backgroundColor: 'rgba(255, 255, 255, 0.1)',
                    },
                  }}
                >
                  {item.label}
                </Button>
              ))}
            </Box>
          )}

          {/* Cart Icon */}
          <IconButton
            color="inherit"
            component={Link}
            to="/cart"
            sx={{ mr: 1 }}
          >
            <Badge badgeContent={itemCount} color="secondary">
              <ShoppingCart />
            </Badge>
          </IconButton>

          {/* User Menu */}
          {!isMobile && (
            <>
              {!isAuthenticated ? (
                <Box sx={{ display: 'flex', gap: 1 }}>
                  <Button 
                    color="inherit" 
                    component={Link} 
                    to="/login"
                    sx={{ 
                      textTransform: 'none',
                      fontSize: '0.95rem',
                      '&:hover': {
                        backgroundColor: 'rgba(255, 255, 255, 0.1)',
                      },
                    }}
                  >
                    Login
                  </Button>
                  <Button 
                    color="inherit" 
                    component={Link} 
                    to="/register"
                    variant="outlined"
                    sx={{ 
                      textTransform: 'none',
                      fontSize: '0.95rem',
                      borderColor: 'rgba(255, 255, 255, 0.5)',
                      '&:hover': {
                        borderColor: 'white',
                        backgroundColor: 'rgba(255, 255, 255, 0.1)',
                      },
                    }}
                  >
                    Register
                  </Button>
                </Box>
              ) : (
                <>
                  <IconButton
                    color="inherit"
                    onClick={handleProfileMenuOpen}
                  >
                    <AccountCircle />
                  </IconButton>
                  <Menu
                    anchorEl={anchorEl}
                    open={Boolean(anchorEl)}
                    onClose={handleMenuClose}
                  >
                    <MenuItem disabled>
                      {user?.firstName} {user?.lastName}
                    </MenuItem>
                    <MenuItem component={Link} to="/profile" onClick={handleMenuClose}>
                      Profile
                    </MenuItem>
                    <MenuItem component={Link} to="/orders" onClick={handleMenuClose}>
                      Orders
                    </MenuItem>
                    <MenuItem onClick={handleLogout}>
                      Logout
                    </MenuItem>
                  </Menu>
                </>
              )}
            </>
          )}
        </Toolbar>
      </Container>

      {/* Mobile Drawer */}
      <Drawer
        anchor="left"
        open={mobileMenuOpen}
        onClose={handleMobileMenuToggle}
      >
        {mobileDrawer}
      </Drawer>
    </AppBar>
  );
};

export default Header;
