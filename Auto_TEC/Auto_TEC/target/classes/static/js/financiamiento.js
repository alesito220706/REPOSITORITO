document.addEventListener('DOMContentLoaded', function() {
      const form = document.getElementById('financiamientoForm');
      
      form.addEventListener('submit', function(e) {
        // Validación básica del lado del cliente
        const nombre = document.getElementById('nombre').value.trim();
        const email = document.getElementById('email').value.trim();
        const modelo = document.getElementById('modelo').value;
        const mensaje = document.getElementById('mensaje').value.trim();
        
        if (!nombre || !email || !modelo || !mensaje) {
          e.preventDefault();
          alert('Por favor, completa todos los campos obligatorios.');
          return false;
        }
        
        // Validación de email básica
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
          e.preventDefault();
          alert('Por favor, ingresa un email válido.');
          return false;
        }
        
        // Si todo está bien, el formulario se enviará normalmente
        return true;
      });
    });